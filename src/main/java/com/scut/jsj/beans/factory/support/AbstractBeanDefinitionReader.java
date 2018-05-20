package com.scut.jsj.beans.factory.support;


import com.scut.jsj.core.io.Resource;
import com.scut.jsj.core.io.ResourceLoader;
import com.scut.jsj.exception.BeanDefinitionStoreException;
import com.scut.jsj.util.Assert;

/**
 * @author jsj
 * @since 2018-4-11
 * BeanDefinitionReader的抽象模板类
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    private final BeanDefinitionRegistry registry;
    private ResourceLoader resourceLoader;


    /**
     * AbstractBeanDefinitionReader 的构造器
     *
     * @param registry 这里的registry既实现了BeanDefinitionRegistry，又实现了ResourceLoader
     */
    public AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        this.registry = registry;
        //这里暂时默认registry instanceof ResourceLoader为true
        if (registry instanceof ResourceLoader) {
            this.resourceLoader = (ResourceLoader) this.registry;
        }
    }

    /**
     * 这里只是提供了一个模板，利用遍历将具体的实现交给了子类的loadBeanDefinitions(Resource iotest)方法
     *
     * @param resources Resource数组
     * @return
     * @throws BeanDefinitionStoreException
     */
    @Override
    public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
        Assert.notNull(resources, "Resource array must not be null");
        int counter = 0;
        for (Resource resource : resources) {
            counter += this.loadBeanDefinitions(resource);
        }
        return counter;
    }

    /**
     * 这里只是提供了一个模板，利用遍历将具体的实现交给了子类的loadBeanDefinitions(String location)方法
     *
     * @param locations
     * @return
     * @throws BeanDefinitionStoreException
     */
    @Override
    public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
        Assert.notNull(locations, "Location array must not be null");
        int counter = 0;
        for (String location : locations) {
            counter += this.loadBeanDefinitions(location);
        }
        return counter;
    }

    /**
     * 最终的调用仍然是loadBeanDefinitions(Resource iotest)方法,交由子类实现
     *
     * @param location
     * @return
     * @throws BeanDefinitionStoreException
     */
    @Override
    public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(this.resourceLoader.getResource(location));
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    @Override
    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
