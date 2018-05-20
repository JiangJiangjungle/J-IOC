package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.PropertyValue;
import com.scut.jsj.beans.PropertyValues;
import com.scut.jsj.beans.factory.BeanFactory;
import com.scut.jsj.beans.factory.ObjectFactory;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.exception.BeanCreationException;
import com.scut.jsj.exception.BeanNotOfRequiredTypeException;
import com.scut.jsj.exception.BeansException;
import com.scut.jsj.exception.NoSuchBeanDefinitionException;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.ObjectUtils;
import com.scut.jsj.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    //缓存所有的DefaultBeanDefinition
    private final Map<String, DefaultBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

    public AbstractBeanFactory() {
    }

    /**
     * AbstractBeanFactory中最重要的方法，即bean的详细获取步骤
     *
     * @param name
     * @param requiredType
     * @param args
     * @param <T>
     * @return
     * @throws BeansException
     */
    protected <T> T doGetBean(String name, Class<T> requiredType, final Object[] args) throws BeansException {
        //删除name中的&字符，得到真正的beanName
        final String beanName = this.transformedBeanName(name);
        //从缓存中取是否有被创建过的单例类型bean，如果有直接取出并返回
        Object sharedInstance = this.getSingleton(name);
        Object bean;
        if (sharedInstance != null && args == null) {
            bean = sharedInstance;
        } else {
            //查找对应的DefaultBeanDefinition
            final DefaultBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
            //获取依赖项
            String[] dependsOn = mbd.getDependsOn();
            String[] var11;
            //若存在依赖项，调用递归直到找到没有依赖项的bean进行创建（重点）
            if (dependsOn != null) {
                var11 = dependsOn;
                //分别处理每一个依赖:若存在循环依赖则报错，否则记录依赖关系
                for (int i = 0; i < dependsOn.length; i++) {
                    String dep = StringUtils.getDependentBeanName(var11[i]);
                    //检查dep是否也（直接或间接）含有依赖项bean，形成循环依赖，若有则报错
                    if (this.isDependent(beanName, dep)) {
                        throw new BeanCreationException(mbd.getDescription(), beanName, "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
                    }
                    //检查是否已经注册过依赖关系
                    if (!this.isDependencyForBean(dep, beanName)) {
                        //记录这一对依赖关系
                        this.registerDependentBean(dep, beanName);
                        //递归，获得这个依赖项
                        this.getBean(dep);
                    }
                }
            }
            //对于没有依赖项的bean(或者依赖项已经全部创建完毕)，继续操作
            if (mbd.isSingleton()) {
                //若mdb是单例则获得（未创建则创建）单例bean
                sharedInstance = this.getSingleton(beanName, new ObjectFactory<Object>() {
                    public Object getObject() throws BeansException {
                        try {
                            //真正的getObject()交给子类实现
                            return AbstractBeanFactory.this.createBean(beanName, mbd, args);
                        } catch (BeansException var2) {
                            //若生成出现异常则销毁
                            AbstractBeanFactory.this.destroySingleton(beanName);
                            throw var2;
                        }
                    }
                });
                //进行检查
                bean = sharedInstance;
            } else {
                //若是多例则创建新的bean
                Object prototypeInstance;
                //创建多例的bean
                prototypeInstance = this.createBean(beanName, mbd, args);
                //进行检查
                bean = prototypeInstance;
            }
        }
        //若有需要则进行类型检查,类型不符合则抛出异常（简化）
        if (requiredType != null && bean != null && !requiredType.isInstance(bean)) {
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        } else {
            return (T) bean;
        }
    }

    public Object getBean(String name) throws BeansException {
        return this.doGetBean(name, (Class) null, (Object[]) null);
    }

    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return this.doGetBean(name, requiredType, (Object[]) null);
    }

    public Object getBean(String name, Object... args) throws BeansException {
        return this.doGetBean(name, (Class) null, args);
    }

    public <T> T getBean(String name, Class<T> requiredType, Object... args) throws BeansException {
        return this.doGetBean(name, requiredType, args);
    }

    /**
     * 根据name删去mergedBeanDefinitions中的beanDefinition记录
     *
     * @param beanName
     */
    protected void clearMergedBeanDefinition(String beanName) {
        this.mergedBeanDefinitions.remove(beanName);
    }

    /**
     * 得到真正的beanName而不是factoryBean的name
     *
     * @param name
     * @return
     */
    protected String transformedBeanName(String name) {
        Assert.notNull(name, "StringNotNull");
        return name.startsWith("&") ? name.substring(1) : name;
    }

    /**
     * 根据beanName得到 DefaultBeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected DefaultBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        //从mergedBeanDefinitions中获取DefaultBeanDefinition
        return this.mergedBeanDefinitions.get(beanName);
    }

    public void setMergedBeanDefinition(String beanName, DefaultBeanDefinition beanDefinition) {
        mergedBeanDefinitions.put(beanName, beanDefinition);
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
    protected Object createBean(String beanName, DefaultBeanDefinition mbd, Object[] args) throws BeanCreationException {
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
    protected Object doCreateBean(final String beanName, final DefaultBeanDefinition mbd, Object[] args) throws BeanCreationException {
        //创建一个bean默认构造实例
        Object exposedObject = this.createBeanInstance(beanName, mbd, args);
        try {
            //此处对bean进行依赖注入
            this.populateBean(beanName, mbd, exposedObject);
            if (exposedObject != null) {
                exposedObject = this.initializeBean(beanName, exposedObject, mbd);
            }
        } catch (Throwable var18) {
            throw new BeanCreationException(mbd.getDescription(), beanName, "Initialization of bean failed", var18);
        }
        try {
            //若是单例对象，将对象存入单例池
            if (mbd.isSingleton()) {
                this.addSingleton(beanName, exposedObject);
            }
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
    private Object initializeBean(String beanName, Object exposedObject, DefaultBeanDefinition mbd) throws BeansException {
        PropertyValues propertyValues = mbd.getPropertyValues();
        //获得bean对应的class对象
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
    private void populateBean(String beanName, DefaultBeanDefinition mbd, Object beanObject) throws BeansException {
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
    protected Object createBeanInstance(String beanName, DefaultBeanDefinition mbd, Object[] args) throws BeanCreationException {
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

    @Override
    public boolean containsBean(String name) {
        return true;
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName);
        if (beanInstance != null) {
            return true;
        } else {
            try {
                DefaultBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                return mbd.isSingleton();
            } catch (BeansException e) {
                return false;
            }
        }
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        return !this.isSingleton(beanName);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName);
        if (beanInstance != null) {
            return beanInstance.getClass();
        } else {
            try {
                DefaultBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                return mbd.getClass();
            } catch (BeansException e) {
                throw new NoSuchBeanDefinitionException("找不到bean的类型！");
            }
        }
    }

    //交给子类实现
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;
}
