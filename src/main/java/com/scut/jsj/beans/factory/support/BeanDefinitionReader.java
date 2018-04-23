package com.scut.jsj.beans.factory.support;


import com.scut.jsj.core.io.Resource;
import com.scut.jsj.core.io.ResourceLoader;
import com.scut.jsj.exception.BeanDefinitionStoreException;

/**
 * @author jsj
 * @since 2018-4-10
 * BeanDefinitionReader用于对BeanDefinition进行解析，本接口定义了规范
 */
public interface BeanDefinitionReader {

    BeanDefinitionRegistry getRegistry();

    ResourceLoader getResourceLoader();

    int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

    /**
     * 用于解析Resource数组中的Resource对象，将信息转换成BeanDefinition对象
     *
     * @param resources Resource数组
     * @return 返回所解析的Resource个数
     * @throws BeanDefinitionStoreException
     */
    int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

    int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;
}
