package com.scut.jsj.beans.factory.support;


import com.scut.jsj.beans.PropertyValue;
import com.scut.jsj.beans.PropertyValues;
import com.scut.jsj.beans.factory.AutowireCapableBeanFactory;
import com.scut.jsj.beans.factory.BeanFactory;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.exception.BeanCreationException;
import com.scut.jsj.exception.BeansException;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.ObjectUtils;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//暂时导入spring的包

/**
 *
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

    private final Map<String, Object> factoryBeanInstanceCache;

    public AbstractAutowireCapableBeanFactory() {
        this.factoryBeanInstanceCache = new ConcurrentHashMap<>(16);

    }

    public AbstractAutowireCapableBeanFactory(BeanFactory parentBeanFactory) {
        this();
    }

    /**
     * createBean的实现方法，调用到此方法时，目标bean的所有依赖项已经全部创建完成
     *
     * @param beanName
     * @param mbd
     * @param args
     * @return
     * @throws BeanCreationException
     */
    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating instance of bean '" + beanName + "'");
        }
        Object beanInstance;
        //又将创建过程委派给了doCreateBean()方法
        beanInstance = this.doCreateBean(beanName, mbd, args);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Finished creating instance of bean '" + beanName + "'");
        }
        return beanInstance;
    }

    /**
     * 真正实现创建bean的方法
     *
     * @param beanName
     * @param mbd
     * @param args
     * @return
     * @throws BeanCreationException
     */
    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
        Object exposedObject = null;
        //先从factoryBeanInstanceCache找是否有创建好的factoryBean实例（暂不实现）
        //若没有就创建一个bean默认构造实例
        if (exposedObject == null) {
            exposedObject = this.createBeanInstance(beanName, mbd, args);
        }
        try {
            //重点关注：此处对bean进行依赖注入
            this.populateBean(beanName, mbd, exposedObject);
            if (exposedObject != null) {
                exposedObject = this.initializeBean(beanName, exposedObject, mbd);
            }
        } catch (Throwable var18) {
            throw new BeanCreationException(mbd.getDescription(), beanName, "Initialization of bean failed", var18);
        }
        try {
            //注册到已完成bean(分单例和多例)的缓存
//            this.registerDisposableBeanIfNecessary(beanName, bean, mbd);
            //若是单例对象，将对象存入单例池
            if (mbd.isSingleton()) {
                this.addSingleton(beanName, exposedObject);
            }
            //若是多例对象，将对象

            return exposedObject;
        } catch (Exception var16) {
            throw new BeanCreationException(mbd.getDescription(), beanName, "Invalid destruction signature", var16);
        }
    }

    /**
     * 对bean的基本属性的赋值
     *
     * @param beanName
     * @param exposedObject
     * @param mbd
     * @return
     */
    private Object initializeBean(String beanName, Object exposedObject, RootBeanDefinition mbd) throws BeansException {


        PropertyValues propertyValues = mbd.getPropertyValues();
        Class<?> beanClass = mbd.getBeanClass();
        //属性名称
        String propertyName;
        //属性值
        Object value;
        //属性类型
        Class<?> propertyType;
        for (PropertyValue propertyValue : propertyValues.getPropertyValues()) {
            propertyName = propertyValue.getName();
            value = propertyValue.getValue();
            propertyType = value.getClass();
            try {
                ObjectUtils.doInvokeSetMethod(propertyName, beanClass, propertyType, exposedObject, value);
            } catch (Exception e) {
                throw new BeansException(beanName + mbd.getResourceDescription() + ": 利用反射调用set方法进行基本属性依赖注入时失败");
            }
        }
        return exposedObject;
    }


    /**
     * 成对bean的引用依赖注入（利用set方法实现）
     *
     * @param beanName
     * @param mbd
     * @param beanObject
     * @throws BeansException
     */
    private void populateBean(String beanName, RootBeanDefinition mbd, Object beanObject) throws BeansException {

        String[] dependencies = mbd.getDependsOn();
        Class<?> beanClass = mbd.getBeanClass();
        //依赖项名称
        String dependentName;
        //依赖项实体
        Object depentObect;
        //bean的字段名
        String fieldName;
        String[] var;
        if (dependencies != null) {
            for (String dependOn : dependencies) {
                var = dependOn.split(";");
                fieldName = var[0];
                dependentName = var[1];
                //依赖项的实例
                depentObect = this.getBean(dependentName);
                try {
                    // 调用set方法完成注入
                    ObjectUtils.doInvokeSetMethod(fieldName, beanClass, depentObect.getClass(), beanObject, depentObect);
                } catch (Exception e) {
                    throw new BeansException(beanName + mbd.getResourceDescription() + ": 利用反射调用set方法进行引用依赖注入时失败");
                }
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("完成 '" + beanName + "' 的依赖注入");
        }
    }

    /**
     * 创建bean的默认构造实例
     *
     * @param beanName
     * @param mbd
     * @param args
     * @return
     * @throws BeanCreationException
     */
    protected Object createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {

        // 确认需要创建Bean实例的类可以实例化(简化)
        Class<?> beanClass = mbd.getBeanClass();
        Assert.notNull(beanClass, "beanClass实例化对象时不允许为null！");
        //若beanClass私有，则抛出异常；否则调用instantiateBean()方法实例化bean
        if (!Modifier.isPublic(beanClass.getModifiers())) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        } else {
            try {
                //返回一个bean的默认构造实例
                return beanClass.newInstance();
            } catch (Exception e) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "反射机制出错，实例化bean失败" + beanClass.getName());
            }
        }
    }

    protected abstract BeanDefinition getBeanDefinition(String var1) throws BeansException;
}
