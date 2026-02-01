package com.naruto.wangyiyunmusic.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.naruto.wangyiyunmusic.config.properties.AudioSecurityProperties;
import com.naruto.wangyiyunmusic.exception.RateLimitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 音频限流服务
 *
 * <p>提供基于IP的多维度限流能力：</p>
 * <ul>
 *   <li>Range请求频率限流（在线播放）</li>
 *   <li>完整下载频率限流（批量下载防护）</li>
 *   <li>不同文件数限流（防止遍历所有歌曲）</li>
 *   <li>并发连接数控制（防止资源耗尽）</li>
 *   <li>动态黑名单机制（自动封禁恶意IP）</li>
 * </ul>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class AudioRateLimitService {

    @Autowired
    private AudioSecurityProperties securityProperties;

    // ========== 数据结构 ==========

    /**
     * IP -> Range请求限流器
     * <p>使用Guava RateLimiter实现令牌桶算法</p>
     */
    private final ConcurrentHashMap<String, RateLimiter> rangeRequestLimiters = new ConcurrentHashMap<>();

    /**
     * IP -> 完整下载限流器
     */
    private final ConcurrentHashMap<String, RateLimiter> fullDownloadLimiters = new ConcurrentHashMap<>();

    /**
     * IP -> 访问的文件名集合（用于统计不同文件数）
     * <p>使用ConcurrentHashMap.newKeySet()创建线程安全的Set</p>
     */
    private final ConcurrentHashMap<String, Set<String>> ipAccessedFiles = new ConcurrentHashMap<>();

    /**
     * IP -> 当前并发连接数
     */
    private final ConcurrentHashMap<String, AtomicInteger> ipConcurrentConnections = new ConcurrentHashMap<>();

    /**
     * IP -> 限流触发次数（违规次数）
     */
    private final ConcurrentHashMap<String, AtomicInteger> ipViolationCounts = new ConcurrentHashMap<>();

    /**
     * 动态黑名单（IP -> 封禁到期时间戳）
     */
    private final ConcurrentHashMap<String, Long> dynamicBlacklist = new ConcurrentHashMap<>();

    /**
     * IP -> 最后访问时间（用于清理过期数据）
     */
    private final ConcurrentHashMap<String, Long> ipLastAccessTime = new ConcurrentHashMap<>();

    // ========== 核心方法 ==========

    /**
     * 检查 IP 是否被限流
     *
     * <p>执行多维度检查：</p>
     * <ol>
     *   <li>黑名单检查</li>
     *   <li>并发连接数检查</li>
     *   <li>不同文件数检查</li>
     *   <li>请求频率检查（Range vs 完整下载）</li>
     * </ol>
     *
     * @param ip IP地址
     * @param isRangeRequest 是否为Range请求
     * @param fileName 文件名
     * @return true-允许访问, false-触发限流（抛出异常）
     * @throws RateLimitException 当触发限流时抛出
     */
    public boolean allowAccess(String ip, boolean isRangeRequest, String fileName) {
        // 1. 检查黑名单
        if (isBlacklisted(ip)) {
            log.warn("IP黑名单拦截: {}", ip);
            throw new RateLimitException(ip, "BLACKLIST", "IP已被封禁，请稍后重试");
        }

        // 2. 检查并发连接数
        if (!checkConcurrentConnections(ip)) {
            incrementViolation(ip);
            throw new RateLimitException(ip, "CONCURRENT", "并发连接数超限");
        }

        // 3. 检查不同文件数限流
        if (!checkUniqueFiles(ip, fileName)) {
            incrementViolation(ip);
            throw new RateLimitException(ip, "UNIQUE_FILES", "访问文件数超限");
        }

        // 4. 检查请求频率限流
        if (!checkRequestRate(ip, isRangeRequest)) {
            incrementViolation(ip);
            String type = isRangeRequest ? "Range请求" : "完整下载";
            throw new RateLimitException(ip, "RATE", type + "频率超限");
        }

        // 5. 记录访问
        recordAccess(ip, fileName);

        return true;
    }

    /**
     * 增加并发连接数
     *
     * <p>在Filter中调用，开始处理请求时调用</p>
     *
     * @param ip IP地址
     */
    public void incrementConnection(String ip) {
        ipConcurrentConnections.computeIfAbsent(ip, k -> new AtomicInteger(0)).incrementAndGet();
        log.debug("IP {} 并发连接数: {}", ip, ipConcurrentConnections.get(ip).get());
    }

    /**
     * 减少并发连接数
     *
     * <p>在Filter中调用，请求处理完成时调用（finally块中）</p>
     *
     * @param ip IP地址
     */
    public void decrementConnection(String ip) {
        AtomicInteger count = ipConcurrentConnections.get(ip);
        if (count != null) {
            int newCount = count.decrementAndGet();
            log.debug("IP {} 并发连接数: {}", ip, newCount);

            // 连接数归零时清理
            if (newCount <= 0) {
                ipConcurrentConnections.remove(ip);
            }
        }
    }

    // ========== 私有方法 ==========

    /**
     * 检查是否在黑名单中
     *
     * <p>检查固定黑名单和动态黑名单</p>
     *
     * @param ip IP地址
     * @return true-在黑名单中, false-不在黑名单中
     */
    private boolean isBlacklisted(String ip) {
        // 检查固定黑名单
        if (securityProperties.getBlacklist().getIps().contains(ip)) {
            log.debug("IP {} 在固定黑名单中", ip);
            return true;
        }

        // 检查动态黑名单
        Long banExpireTime = dynamicBlacklist.get(ip);
        if (banExpireTime != null) {
            if (System.currentTimeMillis() < banExpireTime) {
                log.debug("IP {} 在动态黑名单中，封禁到期时间: {}", ip, banExpireTime);
                return true; // 仍在封禁期
            } else {
                // 封禁期已过，解除封禁
                dynamicBlacklist.remove(ip);
                ipViolationCounts.remove(ip);
                log.info("IP {} 动态黑名单已过期，自动解除封禁", ip);
            }
        }

        return false;
    }

    /**
     * 检查并发连接数
     *
     * @param ip IP地址
     * @return true-未超限, false-超限
     */
    private boolean checkConcurrentConnections(String ip) {
        AtomicInteger count = ipConcurrentConnections.get(ip);
        int maxConnections = securityProperties.getRateLimit().getMaxConcurrentConnections();

        if (count != null && count.get() >= maxConnections) {
            log.warn("IP {} 并发连接数超限: {} >= {}", ip, count.get(), maxConnections);
            return false;
        }

        return true;
    }

    /**
     * 检查不同文件数限流
     *
     * @param ip IP地址
     * @param fileName 文件名
     * @return true-未超限, false-超限
     */
    private boolean checkUniqueFiles(String ip, String fileName) {
        Set<String> files = ipAccessedFiles.computeIfAbsent(ip, k -> ConcurrentHashMap.newKeySet());

        // 临时添加文件名，检查是否超限
        boolean isNewFile = files.add(fileName);
        int maxFiles = securityProperties.getRateLimit().getUniqueFilesPerMinute();

        if (files.size() > maxFiles) {
            log.warn("IP {} 访问文件数超限: {} > {}，新文件: {}", ip, files.size(), maxFiles, isNewFile);
            return false;
        }

        return true;
    }

    /**
     * 检查请求频率限流
     *
     * <p>使用Guava RateLimiter实现令牌桶算法</p>
     *
     * @param ip IP地址
     * @param isRangeRequest 是否为Range请求
     * @return true-未超限, false-超限
     */
    private boolean checkRequestRate(String ip, boolean isRangeRequest) {
        if (isRangeRequest) {
            // Range请求限流（在线播放）
            RateLimiter limiter = rangeRequestLimiters.computeIfAbsent(ip, k -> {
                double permitsPerSecond = securityProperties.getRateLimit().getRangeRequestsPerMinute() / 60.0;
                log.debug("创建Range请求限流器: IP={}, 速率={}/秒", ip, permitsPerSecond);
                return RateLimiter.create(permitsPerSecond);
            });

            boolean allowed = limiter.tryAcquire();
            if (!allowed) {
                log.warn("IP {} Range请求频率超限", ip);
            }
            return allowed;
        } else {
            // 完整下载限流（防止批量下载）
            RateLimiter limiter = fullDownloadLimiters.computeIfAbsent(ip, k -> {
                double permitsPerSecond = securityProperties.getRateLimit().getFullDownloadsPerMinute() / 60.0;
                log.debug("创建完整下载限流器: IP={}, 速率={}/秒", ip, permitsPerSecond);
                return RateLimiter.create(permitsPerSecond);
            });

            boolean allowed = limiter.tryAcquire();
            if (!allowed) {
                log.warn("IP {} 完整下载频率超限", ip);
            }
            return allowed;
        }
    }

    /**
     * 记录访问
     *
     * <p>更新最后访问时间，用于过期数据清理</p>
     *
     * @param ip IP地址
     * @param fileName 文件名
     */
    private void recordAccess(String ip, String fileName) {
        ipLastAccessTime.put(ip, System.currentTimeMillis());
        log.debug("记录访问: IP={}, 文件={}", ip, fileName);
    }

    /**
     * 增加违规次数
     *
     * <p>当达到阈值时，自动加入动态黑名单</p>
     *
     * @param ip IP地址
     */
    private void incrementViolation(String ip) {
        AtomicInteger count = ipViolationCounts.computeIfAbsent(ip, k -> new AtomicInteger(0));
        int newCount = count.incrementAndGet();

        log.warn("IP {} 违规次数: {}", ip, newCount);

        // 检查是否触发黑名单
        int threshold = securityProperties.getRateLimit().getBlacklistThreshold();
        if (newCount >= threshold) {
            addToDynamicBlacklist(ip);
        }
    }

    /**
     * 添加到动态黑名单
     *
     * <p>封禁指定时长，过期后自动解封</p>
     *
     * @param ip IP地址
     */
    private void addToDynamicBlacklist(String ip) {
        int banMinutes = securityProperties.getBlacklist().getBanDurationMinutes();
        long banExpireTime = System.currentTimeMillis() + banMinutes * 60 * 1000L;

        dynamicBlacklist.put(ip, banExpireTime);
        log.warn("⚠️ IP {} 已加入动态黑名单，封禁 {} 分钟（到期时间: {}）",
                 ip, banMinutes, new java.util.Date(banExpireTime));
    }

    /**
     * 定期清理过期数据
     *
     * <p>清理超过窗口时间未访问的IP数据，防止内存泄漏</p>
     * <p>执行间隔：由配置文件指定（默认10分钟）</p>
     */
    @Scheduled(fixedDelayString = "#{@audioSecurityProperties.blacklist.cleanupIntervalMinutes * 60 * 1000}")
    public void cleanupExpiredData() {
        long now = System.currentTimeMillis();
        long expiryTime = securityProperties.getRateLimit().getWindowMinutes() * 60 * 1000L;

        int initialSize = ipLastAccessTime.size();

        ipLastAccessTime.entrySet().removeIf(entry -> {
            boolean expired = now - entry.getValue() > expiryTime;
            if (expired) {
                String ip = entry.getKey();
                log.debug("清理过期IP数据: {}", ip);

                // 清理所有相关数据
                rangeRequestLimiters.remove(ip);
                fullDownloadLimiters.remove(ip);
                ipAccessedFiles.remove(ip);
                ipViolationCounts.remove(ip);
                // 注意：动态黑名单不在此清理，由isBlacklisted()方法处理
            }
            return expired;
        });

        int cleanedCount = initialSize - ipLastAccessTime.size();
        log.info("🧹 过期数据清理完成，清理IP数: {}，当前跟踪IP数: {}",
                 cleanedCount, ipLastAccessTime.size());
    }
}
