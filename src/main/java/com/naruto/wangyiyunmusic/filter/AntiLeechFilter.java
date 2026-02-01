package com.naruto.wangyiyunmusic.filter;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.config.properties.AudioSecurityProperties;
import com.naruto.wangyiyunmusic.exception.AntiLeechException;
import com.naruto.wangyiyunmusic.service.impl.AntiLeechService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 防盗链过滤器
 *
 * <p>拦截音频文件访问请求，执行防盗链检查</p>
 * <p>功能：</p>
 * <ul>
 *   <li>Referer 白名单检查</li>
 *   <li>User-Agent 黑名单检查（拦截爬虫）</li>
 *   <li>IP 黑名单检查</li>
 * </ul>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Component
@Order(1) // 优先级1（最先执行，先于限流Filter）
public class AntiLeechFilter implements Filter {

    @Autowired
    private AntiLeechService antiLeechService;

    @Autowired
    private AudioSecurityProperties securityProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestUri = request.getRequestURI();

        // 只拦截音频文件请求
        if (!requestUri.startsWith("/audio/")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查是否启用防盗链
        if (!securityProperties.getAntiLeech().getEnabled()) {
            log.debug("防盗链功能已禁用，跳过检查");
            chain.doFilter(request, response);
            return;
        }

        String ip = JakartaServletUtil.getClientIP(request);

        log.debug("防盗链检查开始 - IP: {}, URI: {}", ip, requestUri);

        try {
            // 防盗链检查（会抛出AntiLeechException）
            antiLeechService.checkAntiLeech(request, ip);

            // 放行请求
            chain.doFilter(request, response);

        } catch (AntiLeechException e) {
            // 防盗链异常处理
            handleAntiLeechException(response, e);
        }
    }

    /**
     * 处理防盗链异常
     *
     * <p>返回 HTTP 403 状态码和 JSON 响应</p>
     *
     * @param response HTTP响应对象
     * @param e 防盗链异常
     * @throws IOException IO异常
     */
    private void handleAntiLeechException(HttpServletResponse response, AntiLeechException e) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.build(403, e.getMessage(), null);
        response.getWriter().write(objectMapper.writeValueAsString(result));

        log.info("防盗链拦截: IP={}, 原因={}, 消息={}", e.getIp(), e.getReason(), e.getMessage());
    }
}
