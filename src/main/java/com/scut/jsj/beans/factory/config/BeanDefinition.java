package com.scut.jsj.beans.factory.config;

import com.scut.jsj.beans.PropertyValues;

/**
 * @author jsj
 * @since 2018-4-10
 * 将配置文件中的bean配置信息转换成Spring的对象属性信息中来表示，BeanDefinition接口制定了这个规范
 * <p>
 * 需要考虑bean创建顺序的问题：被依赖的bean需要先创建.
 * spring实现：在beanDefinition中有一个String[] getDependsOn()的方法，可以返回依赖bean
 * 的name
 */
public interface BeanDefinition {
    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    //获得所依赖的bean的类名数组
    String[] getDependsOn();

    //获得该bean的className
    String getBeanClassName();

    void setBeanClassName(String beanClassName);

    void setScope(String scope);

    String getScope();

    boolean isSingleton();

    boolean isPrototype();

    String getDescription();

    void setFactoryBeanName(String factoryBeanName);

    String getFactoryBeanName();

    PropertyValues getPropertyValues();

    String getResourceDescription();
}
