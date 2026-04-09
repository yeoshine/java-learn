package com.demo.demo.exception;

/**
 * 自定义业务异常。
 * 当业务校验失败时抛出该异常，统一交给全局异常处理器返回给前端。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
