package com.scut.jsj.factorytest;

import com.scut.jsj.beans.factory.xml.XmlBeanFactory;
import com.scut.jsj.core.io.FileSystemResource;
import com.scut.jsj.core.io.Resource;
import com.scut.jsj.entity.SuperMan;

public class FactoryTest {

    @org.junit.Test
    public void test() throws Exception {
        String path = this.getClass().getResource("/").getPath() + "applicationContext.xml";
        System.out.println(path);
        Resource resource = new FileSystemResource(path);
        //创建XmlBeanFactory
        XmlBeanFactory beanFactory = new XmlBeanFactory(resource);
        Object object = beanFactory.getBean("student");
        Object object2 = beanFactory.getBean("student");
        System.out.println(object.toString());
        System.out.println(object == object2);

        SuperMan superMan = (SuperMan) beanFactory.getBean("superMan");
        System.out.println(superMan.toString());
    }
}
