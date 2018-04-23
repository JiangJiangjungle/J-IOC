package com.scut.jsj.beans.factory.config;

/**
 * 补充了对单例bean的相关操作
 */
public interface SingletonBeanRegistry {
    //将单例bean对象存入单例池（专门存放单例bean的一个容器）
    void registerSingleton(String beanName, Object beanObject);
    //根据name得到单例bean
    Object getSingleton(String beanName);
    //获得所有单例bean的name
    String[] getSingletonNames();
    //获得当前单例bean的数目
    int getSingletonCount();
}
