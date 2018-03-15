package com.scut.jsj.conf.resolve;

import com.scut.jsj.conf.Bean;
import com.scut.jsj.conf.Property;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该类用于读取XML配置文件的信息
 */
public class ConfigurationManager {


    /**
     * 根据指定路径读取配置文件
     *
     * @param path 配置文件的路径
     * @return
     */
    public static Map<String, Bean> getBeanConfig(String path) {
        //用于存放bean配置信息，返回结果
        Map<String, Bean> map = new HashMap<>();
        //创建解析器
        SAXReader saxReader = new SAXReader();
        //加载配置文件
        InputStream inputStream = ConfigurationManager.class.getResourceAsStream(path);
        Document document = null;

        try {
            document = saxReader.read(inputStream);  //读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("加载配置文件出错");
        }
        Element rootElement = document.getRootElement();
        //XPath语句，选取所有bean元素
        String member = "bean";
        List<Element> beans = rootElement.elements(member);
        //遍历bean节点时用到的变量
        String scope;
        Bean bean;
        //遍历property时用到的变量
        List<Element> propertieElements;
        List<Property> beanProperties;
        Property property;
        String valueOrRef;
        //遍历所有bean节点，并将信息封装在Bean配置对象中
        for (Element element : beans) {
            bean = new Bean(element.attributeValue("id"), element.attributeValue("class"));
            scope = element.attributeValue("scope");
            //若指定scope则设置，否则使用默认值
            if (scope != null && scope.length() > 0) {
                bean.setScope(scope);
            }
            member = "property";
            propertieElements = element.elements(member);
            //若该bean节点的property属性不为空，则进行操作
            if (propertieElements != null) {
                //创建对象，用于存放Bean的properties属性
                beanProperties = new ArrayList<Property>();
                //对所有的property节点进行遍历，一一封装并添加至beanProperties中
                for (Element proElement : propertieElements) {
                    property = new Property();

                    property.setName(proElement.attributeValue("name"));
                    valueOrRef = proElement.attributeValue("value");
                    if (valueOrRef != null) {
                        property.setValue(valueOrRef);
                    }
                    valueOrRef = proElement.attributeValue("ref");
                    if (valueOrRef != null) {
                        property.setRef(valueOrRef);
                    }
                    //将封装好的Property对象添加至beanProperties中
                    beanProperties.add(property);
                }
                //添加完毕，加入到所属的Bean配置对象
                bean.setProperties(beanProperties);
            }
            map.put(bean.getId(), bean);
        }
        if (map.size() == 0) {
            return null;
        }
        return map;
    }
}
