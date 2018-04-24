package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.MutablePropertyValues;
import com.scut.jsj.core.io.Resource;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.util.ObjectUtils;

import java.util.Arrays;


/**
 * @author jsj
 * @since 2018-4-10
 * BeanDefinition接口的模板实现（抽象）类
 */
public abstract class AbstractBeanDefinition implements BeanDefinition {
    //bean的作用域
    private String scope = BeanDefinition.SCOPE_SINGLETON;
    //该bean的class对象
    private Object beanClass;
    //所依赖的bean的类名
    private String[] dependsOn;
    //该bean的描述
    private String description;
    //对应的Resource对象
    private Resource resource;
    //属性值
    private MutablePropertyValues propertyValues;
    //工厂名称
    private String factoryBeanName;

    protected AbstractBeanDefinition() {
        super();
    }

    public AbstractBeanDefinition(BeanDefinition original) {
        this.setScope(original.getScope());
        this.setFactoryBeanName(original.getFactoryBeanName());
        this.setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
    }

    /**
     * 添加依赖的bean的类名（因为依赖列表一经确定不再更改，所以使用可变参数列表形成数组）
     *
     * @param dependsOn
     */
    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * 指定beandefiition的所持有的java对象的Class对象
     *
     * @param beanClass
     */
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        Object beanClassObject = this.beanClass;
        if (beanClassObject == null) {
            throw new IllegalStateException("No beans class specified on beans definition: 没有指定bean的Class对象");
        } else if (!(beanClassObject instanceof Class)) {
            throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into" +
                    " an actual Class:不是Class对象");
        }
        return (Class) beanClassObject;
    }

    public void setPropertyValues(MutablePropertyValues propertyValues) {
        this.propertyValues = propertyValues != null ? propertyValues : new MutablePropertyValues();
    }

    public boolean hasBeanClass() {
        return this.beanClass instanceof Class;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof AbstractBeanDefinition)) {
            return false;
        } else {
            AbstractBeanDefinition that = (AbstractBeanDefinition) other;
            if (!ObjectUtils.nullSafeEquals(this.getBeanClassName(), that.getBeanClassName())) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.scope, that.scope)) {
                return false;
            } else if (!Arrays.equals(this.dependsOn, that.dependsOn)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues)) {
                return false;
            } else if (!ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName)) {
                return false;
            } else {
                return super.equals(other);
            }
        }
    }

    @Override
    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String getScope() {
        return scope;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getDependsOn() {
        return dependsOn;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public String getBeanClassName() {
        Object beanClassObject = this.beanClass;
        return beanClassObject instanceof Class ? ((Class) beanClassObject).getName() : (String) beanClassObject;
    }

    @Override
    public boolean isSingleton() {
        return this.scope.equals(BeanDefinition.SCOPE_SINGLETON);
    }

    @Override
    public boolean isPrototype() {
        return !this.isSingleton();
    }

    @Override
    public void setBeanClassName(String className) {
        this.beanClass = className;
    }

    @Override
    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    @Override
    public MutablePropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    @Override
    public String getResourceDescription() {
        return this.resource != null ? this.resource.getDescription() : null;
    }
}
