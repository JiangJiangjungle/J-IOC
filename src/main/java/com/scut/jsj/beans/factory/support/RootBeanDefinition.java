package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.factory.config.BeanDefinition;

/**
 * root(直接继承于Object类的)bean的实现类，继承了AbstractBeanDefinition抽象类
 */
public class RootBeanDefinition extends AbstractBeanDefinition {
    private boolean allowCaching = true;
    private boolean isFactoryMethodUnique = false;

    public RootBeanDefinition() {
        super();
    }

    public RootBeanDefinition(BeanDefinition original) {
        super(original);
    }

    public RootBeanDefinition(RootBeanDefinition original) {
        super(original);
        this.allowCaching = original.allowCaching;
        this.isFactoryMethodUnique = original.isFactoryMethodUnique;
    }

    public RootBeanDefinition cloneBeanDefinition() {
        return new RootBeanDefinition(this);
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public void setParentName(String parentName) {
        if (parentName != null) {
            throw new IllegalArgumentException("Root beans cannot be changed into a child beans with parent reference" +
                    "root beans 不可能设置一个除Object外的父类");
        }
    }

}
