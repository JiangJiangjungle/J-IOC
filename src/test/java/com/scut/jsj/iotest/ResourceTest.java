package com.scut.jsj.iotest;

import com.scut.jsj.beans.PropertyValue;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.beans.factory.xml.XmlParser;
import com.scut.jsj.core.io.FileSystemResource;
import com.scut.jsj.core.io.Resource;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.util.Map;

public class ResourceTest {

    @org.junit.Test
    public void test() throws Exception {
        String path = this.getClass().getResource("/").getPath() + "aaa.xml";
        Resource resource = new FileSystemResource(path);
        //创建SAXReader对象
        SAXReader reader = new SAXReader();
        //读取文件 转换成Document
        Document document = reader.read(resource.getFile());
        Map<String, BeanDefinition> map = XmlParser.parser(document, resource);
        BeanDefinition beanDefinition;
        for (Map.Entry<String, BeanDefinition> entry : map.entrySet()) {
            beanDefinition = entry.getValue();
            System.out.println(beanDefinition.getDescription());
            System.out.println("  properties:");
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues().getPropertyValues()) {
                System.out.println("    " + propertyValue.toString());
            }
            if (beanDefinition.getDependsOn() != null) {
                System.out.println("  dependsOn:");
                for (String d : beanDefinition.getDependsOn()) {
                    System.out.println("    " + d);
                }
            }
        }
    }
}
