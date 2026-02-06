package com.cjy.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cjy.common.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理所有@Valid校验失败异常
     * 当 @RequestBody 参数的校验注解（如 @NotBlank、@Pattern）验证失败时抛出
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        // 获取所有字段验证错误，返回第一个错误信息
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.error(message);
    }

    /**
     * 处理所有其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace(); // 打印到控制台
        return Result.error("服务器错误: " + e.getMessage());
    }

}
