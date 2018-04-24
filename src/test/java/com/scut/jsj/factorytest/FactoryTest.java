package com.scut.jsj.factorytest;

import com.scut.jsj.beans.factory.xml.XmlBeanFactory;
import com.scut.jsj.core.io.FileSystemResourceLoader;
import com.scut.jsj.core.io.Resource;

public class FactoryTest {

    @org.junit.Test
    public void test() throws Exception {
        String path = this.getClass().getResource("/").getPath() + "aaa.xml";
        FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
        Resource resource = resourceLoader.getResource(path);
        //创建XmlBeanFactory
        XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
        Object object = beanFactory.getBean("student");
        Object object2 = beanFactory.getBean("student");
        System.out.println(object.toString());
        System.out.println(object == object2);
    }
}
