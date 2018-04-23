package com.scut.jsj.beans.factory.xml;

import com.scut.jsj.beans.factory.BeanFactory;
import com.scut.jsj.beans.factory.support.DefaultListableBeanFactory;
import com.scut.jsj.beans.factory.support.XmlBeanDefinitionReader;
import com.scut.jsj.core.io.Resource;
import com.scut.jsj.exception.BeansException;

public class XmlBeanFactory extends DefaultListableBeanFactory {
    private final XmlBeanDefinitionReader reader;

    public XmlBeanFactory(Resource resource) throws BeansException {
        this(resource, (BeanFactory) null);
    }

    public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
        super(parentBeanFactory);
        this.reader = new XmlBeanDefinitionReader(this);
        this.reader.loadBeanDefinitions(resource);
    }
}
