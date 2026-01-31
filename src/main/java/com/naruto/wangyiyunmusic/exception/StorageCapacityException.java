package com.naruto.wangyiyunmusic.exception;

/**
 * 存储容量异常
 *
 * <p>当存储空间不足或超出限制时抛出此异常</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public class StorageCapacityException extends BusinessException {

    public StorageCapacityException(String message) {
        super(message);
    }

    public StorageCapacityException(String message, Throwable cause) {
        super(message, cause);
    }
}
