package com.scut.jsj.beans.factory;

/**
 * 主要是实现bean的list集合操作功能，暂时不补充
 */
public interface ListableBeanFactory extends BeanFactory {

    boolean containsBeanDefinition(String beanName);
}
