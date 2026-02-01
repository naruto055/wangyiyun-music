package com.naruto.wangyiyunmusic.exception;

import com.naruto.wangyiyunmusic.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 参数校验异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        log.error("参数校验失败", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return Result.error(message);
    }

    /**
     * 视频解析异常
     */
    @ExceptionHandler(VideoParseException.class)
    public Result<Void> handleVideoParseException(VideoParseException e) {
        log.error("视频解析异常: {}", e.getMessage(), e);
        return Result.error("视频解析失败: " + e.getMessage());
    }

    /**
     * 文件验证异常
     */
    @ExceptionHandler(FileValidationException.class)
    public Result<Void> handleFileValidationException(FileValidationException e) {
        log.error("文件验证异常: {}", e.getMessage());
        return Result.error("文件验证失败: " + e.getMessage());
    }

    /**
     * 存储容量异常
     */
    @ExceptionHandler(StorageCapacityException.class)
    public Result<Void> handleStorageCapacityException(StorageCapacityException e) {
        log.error("存储容量异常: {}", e.getMessage());
        return Result.error("存储空间不足: " + e.getMessage());
    }

    /**
     * 限流异常处理
     */
    @ExceptionHandler(RateLimitException.class)
    public Result<Void> handleRateLimitException(RateLimitException e, HttpServletResponse response) {
        log.warn("限流触发 - IP: {}, 类型: {}, 消息: {}", e.getIp(), e.getLimitType(), e.getMessage());
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
        return Result.build(429, e.getMessage(), null);
    }

    /**
     * 防盗链异常处理
     */
    @ExceptionHandler(AntiLeechException.class)
    public Result<Void> handleAntiLeechException(AntiLeechException e, HttpServletResponse response) {
        log.warn("防盗链拦截 - IP: {}, 原因: {}, 消息: {}", e.getIp(), e.getReason(), e.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
        return Result.build(403, e.getMessage(), null);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}
