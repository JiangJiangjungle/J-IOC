package com.scut.jsj.beanfactory;

import com.scut.jsj.beanfatory.ClassPathXmlApplicationContext;
import org.junit.Test;

import java.util.Map;

public class TestClassPathXmlApplicationContext {

    private final String PATH = "/applicationcontext.xml";
    private ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(PATH);

    @Test
    public void test() {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = this.classPathXmlApplicationContext;
        Map<String, Object> context = classPathXmlApplicationContext.getContext();

        for (Map.Entry<String, Object> entry : context.entrySet()) {
            System.out.println("bean的name:" + entry.getKey());
            System.out.println("bean:" + entry.getValue().toString());
        }
    }

    @Test
    public void testGetBean() {
        ClassPathXmlApplicationContext classPathXmlApplicationContext = this.classPathXmlApplicationContext;
        String beanID = "user";
        Object object1 = classPathXmlApplicationContext.getBean(beanID);
        Object object2 = classPathXmlApplicationContext.getBean(beanID);
        System.out.println(beanID + "是否为单例对象?");
        System.out.println(object1 == object2);

        beanID = "pet";
        object1 = classPathXmlApplicationContext.getBean(beanID);
        object2 = classPathXmlApplicationContext.getBean(beanID);
        System.out.println(beanID + "是否为单例对象?");
        System.out.println(object1 == object2);
    }
}
