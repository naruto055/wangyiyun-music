package com.naruto.wangyiyunmusic.service.impl;

import cn.hutool.core.util.StrUtil;
import com.naruto.wangyiyunmusic.config.properties.AudioSecurityProperties;
import com.naruto.wangyiyunmusic.exception.AntiLeechException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 防盗链服务
 *
 * <p>提供 Referer、User-Agent、IP 的多重检查能力</p>
 * <p>防止以下行为：</p>
 * <ul>
 *   <li>其他网站盗链音频资源（Referer检查）</li>
 *   <li>爬虫批量下载（User-Agent检查）</li>
 *   <li>已知恶意IP访问（IP黑名单）</li>
 * </ul>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class AntiLeechService {

    @Autowired
    private AudioSecurityProperties securityProperties;

    /**
     * 检查请求是否通过防盗链验证
     *
     * <p>执行多重检查：</p>
     * <ol>
     *   <li>IP黑名单检查</li>
     *   <li>User-Agent黑名单检查</li>
     *   <li>Referer白名单检查</li>
     * </ol>
     *
     * @param request HTTP请求
     * @param ip 客户端IP
     * @return true-通过, false-拦截（抛出异常）
     * @throws AntiLeechException 当被拦截时抛出
     */
    public boolean checkAntiLeech(HttpServletRequest request, String ip) {
        // 1. 检查IP黑名单
        if (!checkIpBlacklist(ip)) {
            log.warn("防盗链拦截 - IP黑名单: {}", ip);
            throw new AntiLeechException(ip, "IP_BLACKLIST", "IP已被禁止访问");
        }

        // 2. 检查 User-Agent 黑名单
        String userAgent = StrUtil.nullToDefault(request.getHeader("User-Agent"), "");
        if (!checkUserAgent(userAgent, ip)) {
            log.warn("防盗链拦截 - User-Agent黑名单: IP={}, UA={}", ip, userAgent);
            throw new AntiLeechException(ip, "USER_AGENT", "请使用正常的浏览器访问");
        }

        // 3. 检查 Referer 白名单
        String referer = StrUtil.nullToDefault(request.getHeader("Referer"), "");
        if (!checkReferer(referer, ip)) {
            log.warn("防盗链拦截 - Referer不在白名单: IP={}, Referer={}", ip, referer);
            throw new AntiLeechException(ip, "REFERER", "禁止外部引用");
        }

        log.debug("防盗链检查通过: IP={}", ip);
        return true;
    }

    /**
     * 检查IP是否在黑名单中
     *
     * @param ip IP地址
     * @return true-不在黑名单（允许）, false-在黑名单（拒绝）
     */
    private boolean checkIpBlacklist(String ip) {
        List<String> blacklistIps = securityProperties.getBlacklist().getIps();

        if (blacklistIps.contains(ip)) {
            log.debug("IP {} 在固定黑名单中", ip);
            return false;
        }

        return true;
    }

    /**
     * 检查 User-Agent 是否合法
     *
     * <p>拒绝以下情况：</p>
     * <ul>
     *   <li>空 User-Agent</li>
     *   <li>匹配黑名单正则表达式（爬虫、下载工具等）</li>
     * </ul>
     *
     * @param userAgent User-Agent字符串
     * @param ip IP地址（用于日志）
     * @return true-合法, false-不合法
     */
    private boolean checkUserAgent(String userAgent, String ip) {
        // 拒绝空 User-Agent
        if (userAgent == null || userAgent.isEmpty()) {
            log.debug("空 User-Agent: IP={}", ip);
            return false;
        }

        List<String> blockedPatterns = securityProperties.getAntiLeech().getBlockedUserAgents();

        // 检查是否匹配黑名单正则表达式
        for (String pattern : blockedPatterns) {
            try {
                if (userAgent.matches(pattern)) {
                    log.debug("User-Agent匹配黑名单: IP={}, UA={}, 模式={}", ip, userAgent, pattern);
                    return false; // 匹配黑名单
                }
            } catch (Exception e) {
                log.error("User-Agent正则匹配失败: pattern={}, UA={}", pattern, userAgent, e);
            }
        }

        return true;
    }

    /**
     * 检查 Referer 是否在白名单中
     *
     * <p>检查逻辑：</p>
     * <ol>
     *   <li>如果允许空Referer，则空Referer放行</li>
     *   <li>检查Referer是否包含白名单中的域名</li>
     * </ol>
     *
     * @param referer Referer字符串
     * @param ip IP地址（用于日志）
     * @return true-在白名单（允许）, false-不在白名单（拒绝）
     */
    private boolean checkReferer(String referer, String ip) {
        // 1. 允许空 Referer（直接访问）
        if (referer == null || referer.isEmpty()) {
            boolean allowEmpty = securityProperties.getAntiLeech().getAllowEmptyReferer();
            if (allowEmpty) {
                log.debug("空 Referer 放行: IP={}", ip);
                return true;
            } else {
                log.debug("空 Referer 拒绝: IP={}", ip);
                return false;
            }
        }

        // 2. 检查白名单
        List<String> allowedReferers = securityProperties.getAntiLeech().getAllowedReferers();

        for (String allowed : allowedReferers) {
            if (referer.contains(allowed)) {
                log.debug("Referer 白名单匹配: IP={}, Referer={}, 白名单={}", ip, referer, allowed);
                return true;
            }
        }

        log.debug("Referer 不在白名单: IP={}, Referer={}", ip, referer);
        return false;
    }
}
