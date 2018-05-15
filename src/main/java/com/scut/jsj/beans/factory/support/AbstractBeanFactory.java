package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.factory.BeanFactory;
import com.scut.jsj.beans.factory.ObjectFactory;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.exception.*;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

    public AbstractBeanFactory() {
    }

    /**
     * AbstractBeanFactory中最重要的方法，即bean的真正获取方法步骤
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
            bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, null);
        } else {
            //查找对应的RootBeanDefinition
            final RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
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
                bean = this.getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
            } else {
                //若是多例则创建新的bean
                Object prototypeInstance;
                //创建多例的bean
                prototypeInstance = this.createBean(beanName, mbd, args);
                //进行检查
                bean = this.getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
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
     * 若需要判断需要得到的bean是否FactoryBean类型：
     * 1.是，且name以&开头（不以&开头则从FactoryBean的getObjectFromFactoryBean()方法中得到真正的bean）就返回该factoryBean；
     * 2.否，且name不以&开头，直接返回bean（因为已经是需要的bean）。
     * 3.对于bean既不是FactoryBean类型，name又以&开头的情况，抛出异常（不符合规范）
     * <p>
     * 暂时不实现以上功能，以后完善
     *
     * @param sharedInstance
     * @param name
     * @param beanName
     * @param rootBeanDefinition
     * @return
     */
    protected Object getObjectForBeanInstance(Object sharedInstance, String name, String beanName, RootBeanDefinition rootBeanDefinition) {
        return sharedInstance;
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
     * 根据beanName得到 RootBeanDefinition
     *
     * @param beanName
     * @return
     * @throws BeansException
     */
    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        //从mergedBeanDefinitions中获取RootBeanDefinition
        return this.mergedBeanDefinitions.get(beanName);
    }

    public void setMergedBeanDefinition(String beanName, RootBeanDefinition beanDefinition) {
        mergedBeanDefinitions.put(beanName, beanDefinition);
    }

    //交给子类实现
    protected abstract Object createBean(String var1, RootBeanDefinition var2, Object[] var3) throws BeanCreationException;

    //交给子类实现
    protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        String beanName = this.transformedBeanName(name);
        Object beanInstance = this.getSingleton(beanName);
        if (beanInstance != null) {
            return true;
        } else {
            try {
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
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
                RootBeanDefinition mbd = this.getMergedLocalBeanDefinition(beanName);
                return mbd.getClass();
            } catch (BeansException e) {
                throw new NoSuchBeanDefinitionException("找不到bean的类型！");
            }
        }
    }

    @Override
    public boolean containsBean(String name) {
        return true;
    }
}
