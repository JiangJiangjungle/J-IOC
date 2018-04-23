package com.scut.jsj.exception;


public class BeanNotOfRequiredTypeException extends BeansException {
    private String beanName;
    private Class<?> requiredType;
    private Class<?> actualType;

    public BeanNotOfRequiredTypeException(String beanName, Class<?> requiredType, Class<?> actualType) {
        super("Bean named '" + beanName + "' is expected to be of type '" + requiredType.getName() + "' but was actually of type '" + "is wrong'");
        this.beanName = beanName;
        this.requiredType = requiredType;
        this.actualType = actualType;
    }

    public String getBeanName() {
        return this.beanName;
    }

    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    public Class<?> getActualType() {
        return this.actualType;
    }
}
