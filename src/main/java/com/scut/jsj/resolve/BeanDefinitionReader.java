package com.scut.jsj.resolve;

import com.scut.jsj.beanfatory.BeanFactory;
import com.scut.jsj.carrier.BeanDefinition;
import com.scut.jsj.carrier.PropertyValue;
import com.scut.jsj.io.ClassPathResource;
import lombok.Data;
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
@Data
public class BeanDefinitionReader {

    //所有节点
    static final String BEAN = "bean";
    static final String PROPERTY = "property";
    //<property> 节点属性
    static final String NAME = "name";
    static final String VALUE = "value";
    static final String REF = "ref";
    //<bean> 节点属性
    static final String ID = "id";
    static final String CLASS = "class";


    private BeanFactory beanFactory;

    public BeanDefinitionReader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * 根据指定路径读取配置文件
     *
     * @param resource
     * @return
     */
    public static Map<String, BeanDefinition> loadBeanDefinitions(ClassPathResource resource) {
        //获取配置文件路径
        String path = resource.getPath();
        //用于存放bean配置信息，返回结果
        Map<String, BeanDefinition> map = new HashMap<>();
        //创建解析器
        SAXReader saxReader = new SAXReader();
        //加载配置文件
        InputStream inputStream = BeanDefinitionReader.class.getResourceAsStream(path);
        Document document;
        try {
            document = saxReader.read(inputStream);  //读取XML文件,获得document对象
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("加载配置文件出错");
        }
        Element rootElement = document.getRootElement();
        //XPath语句，选取所有bean元素
        List<Element> beans = rootElement.elements(BEAN);
        //遍历bean节点时用到的变量
        String scope;
        BeanDefinition beanDefinition;
        //遍历property时用到的变量
        List<Element> propertieElements;
        List<PropertyValue> beanProperties;
        PropertyValue propertyValue;
        String valueOrRef;
        //遍历所有bean节点，并将信息封装在Bean配置对象中
        for (Element element : beans) {
            beanDefinition = new BeanDefinition(element.attributeValue(ID), element.attributeValue(CLASS));
            scope = element.attributeValue("scope");
            //若指定scope则设置，否则使用默认值
            if (scope != null && scope.length() > 0) {
                beanDefinition.setScope(scope);
            }
            propertieElements = element.elements(PROPERTY);
            //若该bean节点的property属性不为空，则进行操作
            if (propertieElements != null) {
                //创建对象，用于存放Bean的properties属性
                beanProperties = new ArrayList<PropertyValue>();
                //对所有的property节点进行遍历，一一封装并添加至beanProperties中
                for (Element proElement : propertieElements) {
                    propertyValue = new PropertyValue();
                    propertyValue.setName(proElement.attributeValue(NAME));
                    valueOrRef = proElement.attributeValue(VALUE);
                    if (valueOrRef != null) {
                        propertyValue.setValue(valueOrRef);
                    } else {
                        valueOrRef = proElement.attributeValue(REF);
                        propertyValue.setValue(valueOrRef);
                        propertyValue.setReference(true);
                    }
                    //将封装好的Property对象添加至beanProperties中
                    beanProperties.add(propertyValue);
                }
                //添加完毕，加入到所属的Bean配置对象
                beanDefinition.setProperties(beanProperties);
            }
            map.put(beanDefinition.getId(), beanDefinition);
        }
        if (map.size() == 0) {
            return null;
        }
        return map;
    }
}
