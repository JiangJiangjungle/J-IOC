package com.scut.jsj.exception;

public class NoSuchBeanDefinitionException extends BeansException {

    public NoSuchBeanDefinitionException() {
    }

    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }
}
