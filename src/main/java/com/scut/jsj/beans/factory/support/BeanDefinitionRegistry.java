package com.scut.jsj.beans.factory.support;

import com.scut.jsj.exception.BeanDefinitionStoreException;
import com.scut.jsj.beans.factory.config.BeanDefinition;

/**
 * 作为一个BeanDefinition的注册表，本接口定义了注册、注销等相关方法
 */
public interface BeanDefinitionRegistry {

    //BeanDefinition的注册
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

    //撤销BeanDefinition的注册
    void removeBeanDefinition(String beanName) throws Exception;

    //根据beanName获得BeanDefinition
    BeanDefinition getBeanDefinition(String beanName) throws Exception;

    String[] getBeanDefinitionNames();

    boolean containsBeanDefinition(String beanName);

    int getBeanDefinitionCount();
}
