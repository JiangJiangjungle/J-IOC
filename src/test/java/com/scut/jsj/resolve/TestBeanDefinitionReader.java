package com.scut.jsj.resolve;

import com.scut.jsj.carrier.BeanDefinition;
import com.scut.jsj.carrier.PropertyValue;
import com.scut.jsj.io.ClassPathResource;
import org.junit.Test;

import java.util.Map;

public class TestBeanDefinitionReader {

    private final String PATH = "/applicationcontext.xml";

    @Test
    public void test() {
        ClassPathResource resource = new ClassPathResource(PATH);
        Map<String, BeanDefinition> properties = BeanDefinitionReader.loadBeanDefinitions(resource);
        if (properties != null) {
            for (Map.Entry<String, BeanDefinition> entry : properties.entrySet()) {
                System.out.println(entry.getKey());
                for (PropertyValue propertyValue : entry.getValue().getProperties()) {
                    System.out.println("  " + propertyValue.toString());
                }
            }
        }
    }
}
