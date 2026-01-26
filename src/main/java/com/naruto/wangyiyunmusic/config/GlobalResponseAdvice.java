package com.naruto.wangyiyunmusic.config;

import com.naruto.wangyiyunmusic.annotation.IgnoreResponseWrap;
import com.naruto.wangyiyunmusic.common.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应处理器
 *
 * <p>功能：</p>
 * <ul>
 *   <li>自动封装 Controller 返回值为统一 Result 格式</li>
 *   <li>支持通过 @IgnoreResponseWrap 注解排除特殊接口</li>
 * </ul>
 *
 * <p>注意：异常处理由 {@link com.naruto.wangyiyunmusic.exception.GlobalExceptionHandler} 统一处理</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-26
 */
@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 判断是否需要处理响应体
     *
     * @param returnType    返回类型
     * @param converterType 转换器类型
     * @return true 表示需要处理，false 表示跳过
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查方法或类上是否有 @IgnoreResponseWrap 注解
        return !returnType.hasMethodAnnotation(IgnoreResponseWrap.class)
                && !returnType.getDeclaringClass().isAnnotationPresent(IgnoreResponseWrap.class);
    }

    /**
     * 在响应体写入之前进行处理
     *
     * @param body                  原始返回值
     * @param returnType            返回类型
     * @param selectedContentType   选择的内容类型
     * @param selectedConverterType 选择的转换器类型
     * @param request               HTTP 请求
     * @param response              HTTP 响应
     * @return 处理后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 如果返回值已经是 Result 类型，直接返回
        if (body instanceof Result) {
            return body;
        }

        // 如果返回值为 null，返回空 Result
        if (body == null) {
            return Result.success();
        }

        // 特殊处理 String 类型，避免类型转换问题
        if (body instanceof String) {
            return Result.success(body);
        }

        // 自动封装为 Result
        return Result.success(body);
    }
}
