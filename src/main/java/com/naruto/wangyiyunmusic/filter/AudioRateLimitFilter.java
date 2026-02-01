package com.naruto.wangyiyunmusic.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.config.properties.AudioSecurityProperties;
import com.naruto.wangyiyunmusic.exception.RateLimitException;
import com.naruto.wangyiyunmusic.service.impl.AudioRateLimitService;
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
 * 音频限流过滤器
 *
 * <p>拦截音频文件访问请求，执行限流检查</p>
 * <p>功能：</p>
 * <ul>
 *   <li>多维度限流检查（频率、并发、文件数）</li>
 *   <li>动态黑名单拦截</li>
 *   <li>并发连接数管理（增加/释放）</li>
 * </ul>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Component
@Order(2) // 优先级2（防盗链Filter为1，先执行防盗链检查）
public class AudioRateLimitFilter implements Filter {

    @Autowired
    private AudioRateLimitService rateLimitService;

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

        // 检查是否启用限流
        if (!securityProperties.getRateLimit().getEnabled()) {
            log.debug("限流功能已禁用，跳过检查");
            chain.doFilter(request, response);
            return;
        }

        String ip = JakartaServletUtil.getClientIP(request);
        boolean isRangeRequest = StrUtil.isNotBlank(request.getHeader("Range"));
        String fileName = StrUtil.subAfter(requestUri, '/', true);

        log.debug("限流检查开始 - IP: {}, 文件: {}, Range: {}", ip, fileName, isRangeRequest);

        try {
            // 1. 限流检查（会抛出RateLimitException）
            rateLimitService.allowAccess(ip, isRangeRequest, fileName);

            // 2. 增加并发连接数
            rateLimitService.incrementConnection(ip);

            try {
                // 3. 放行请求
                chain.doFilter(request, response);
            } finally {
                // 4. 减少并发连接数（无论请求成功或失败）
                rateLimitService.decrementConnection(ip);
            }

        } catch (RateLimitException e) {
            // 限流异常处理
            handleRateLimitException(response, e);
        }
    }

    /**
     * 处理限流异常
     *
     * <p>返回 HTTP 429 状态码和 JSON 响应</p>
     *
     * @param response HTTP响应对象
     * @param e 限流异常
     * @throws IOException IO异常
     */
    private void handleRateLimitException(HttpServletResponse response, RateLimitException e) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
        response.setContentType("application/json;charset=UTF-8");

        Result<Void> result = Result.build(429, e.getMessage(), null);
        response.getWriter().write(objectMapper.writeValueAsString(result));

        log.info("限流拦截: IP={}, 类型={}, 消息={}", e.getIp(), e.getLimitType(), e.getMessage());
    }
}
