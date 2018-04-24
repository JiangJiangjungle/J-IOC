package com.scut.jsj.beans.factory.support;


import com.scut.jsj.beans.factory.BeanFactory;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.exception.BeanDefinitionStoreException;
import com.scut.jsj.exception.BeansException;
import com.scut.jsj.exception.NoSuchBeanDefinitionException;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jsj
 * @since 2018-4-23
 * 这是BeanFactory的最终默认实现类,另外实现了BeanDefinitionRegistry接口，将BeanFactory与BeanDefinitionReader彻底连接了起来
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, Serializable {
    //用于缓存所有的beanDefinition
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    //用于缓存所有的beanDefinition对应的name
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

    public DefaultListableBeanFactory() {
    }

    public boolean containsBeanDefinition(String beanName) {
        Assert.notNull(beanName, "Bean name must not be null");
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        Assert.hasText(beanName, "Bean name must not be empty");
        Assert.notNull(beanDefinition, "BeanDefinition must not be null");
        BeanDefinition oldBeanDefinition = this.beanDefinitionMap.get(beanName);
        if (oldBeanDefinition != null) {
            //验证 beanDefinition 与 oldBeanDefinition 是否相等
            if (!beanDefinition.equals(oldBeanDefinition)) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
                }
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
            }
            //默认允许覆盖原先已经存在的BeanDefinition
            this.beanDefinitionMap.put(beanName, beanDefinition);
        } else {
            //若原先为空
            this.beanDefinitionMap.put(beanName, beanDefinition);
            this.beanDefinitionNames.add(beanName);
        }
        beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition instanceof RootBeanDefinition) {
            this.setMergedBeanDefinition(beanName, (RootBeanDefinition) beanDefinition);
        }
//        //若 oldBeanDefinition
//        if (oldBeanDefinition != null || this.containsSingleton(beanName)) {
//            this.resetBeanDefinition(beanName);
//        }
    }

    protected void resetBeanDefinition(String beanName) {
        //此处调用了两个上层方法
        this.clearMergedBeanDefinition(beanName);
        this.destroySingleton(beanName);
    }


    @Override
    public void removeBeanDefinition(String beanName) throws Exception {
        Assert.hasText(beanName, "'beanName' must not be empty");
        BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        } else {
            this.beanDefinitionNames.remove(beanName);
            this.resetBeanDefinition(beanName);
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("No bean named '" + beanName + "' found in " + this);
            }
            throw new NoSuchBeanDefinitionException(beanName);
        } else {
            return bd;
        }
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return StringUtils.toStringArray(this.beanDefinitionNames);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

}
