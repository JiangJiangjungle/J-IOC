package com.scut.jsj.beans.factory.xml;

import com.scut.jsj.beans.MutablePropertyValues;
import com.scut.jsj.beans.PropertyValue;
import com.scut.jsj.beans.factory.annotation.Autowired;
import com.scut.jsj.beans.factory.annotation.Component;
import com.scut.jsj.beans.factory.annotation.Qualifier;
import com.scut.jsj.beans.factory.annotation.Scope;
import com.scut.jsj.beans.factory.config.BeanDefinition;
import com.scut.jsj.beans.factory.support.AbstractBeanDefinition;
import com.scut.jsj.beans.factory.support.RootBeanDefinition;
import com.scut.jsj.core.io.Resource;
import com.scut.jsj.exception.BeanDefinitionStoreException;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.ClassUtils;
import com.scut.jsj.util.ObjectUtils;
import com.scut.jsj.util.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 用于对XML文件进行解析，将bean的所有信息解析成BeanDefinition的形式
 */
public class XmlParser {
    private static final String PACKAGE_SCAN = "package-scan";
    private static final String BEAN_ELEMENT = "bean";
    private static final String PROPERTY_ELEMENT = "property";

    private static Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

    /**
     * 解析一个Document中所有的bean节点和Autowire注解bean，并存入beanDefinitions缓存中
     *
     * @param document
     * @param resource
     * @return
     */
    public static Map<String, BeanDefinition> parser(Document document, Resource resource) throws BeanDefinitionStoreException {
        Element root = document.getRootElement();// 获得根节点
        //判断是否为使用spring的bean规则的xml文件
        Attribute attribute = root.attribute("xmlnamespace");
        if (!attribute.getValue().equals("spring/schma/beans")) {
            throw new BeanDefinitionStoreException("不是使用spring-bean规则的xml文件");
        }
        @SuppressWarnings("unchecked")
        List<Element> beans = root.elements();
        if (beans == null) {
            throw new BeanDefinitionStoreException("解析无效的xml文件！");
        }
        //用于获取bean的名称
        String beanName;
        AbstractBeanDefinition beanDefinition;
        for (Element bean : beans) {
            //若不是<bean>节点
            if (!bean.getName().equals(BEAN_ELEMENT)) {
                //检查是否<package-scan>节点
                if (bean.getName().equals(PACKAGE_SCAN)) {
                    //扫描包中所有注解bean
                    attribute = bean.attribute("package_name");
                    String packageName = attribute.getValue();
                    scanAndparse(packageName, resource);
                    continue;
                } else {
                    throw new BeanDefinitionStoreException("配置了错误的xml文件节点");
                }
            }
            //若<bean>节点中id或class属性为空则配置不正确
            attribute = bean.attribute("id");
            Assert.notNull(attribute, "bean节点的id不能为空");
            beanName = attribute.getValue();

            beanDefinition = doParse(bean, beanName, resource);
            //将解析完成的beanDefinition添加到已解析缓存中
            beanDefinitions.put(beanName, beanDefinition);
        }
        return beanDefinitions;
    }

    /**
     * 扫描并解析packageName包中所有带@Component注解的类
     *
     * @param packageName
     * @throws BeanDefinitionStoreException
     */
    public static void scanAndparse(String packageName, Resource resource) throws BeanDefinitionStoreException {
        String packageLocation = Class.class.getClass().getResource("/").getPath();
        try {
            //扫描packageLocation下所有类，并得到类名数组
            String[] classNames = ClassUtils.getPackageAllClassName(packageLocation, packageName);
            Class beanClass;
            //注解类
            Component component;
            Autowired autowired;
            Qualifier qualifier;
            Scope scope;
            //beanDefinition
            AbstractBeanDefinition beanDefinition;
            //将所有依赖项存入dependSet中
            Set<String> dependSet = null;
            //所有基本属性存入propertyValues中
            MutablePropertyValues propertyValues = null;
            PropertyValue propertyValue;
            String propertyName;
            //类路径
            String classPath;
            for (String className : classNames) {
                classPath = packageName + "." + className;
                beanClass = Class.forName(classPath);
                component = (Component) beanClass.getAnnotation(Component.class);
                if (component != null) {
                    //存在@Component注解的类，进行解析
                    beanDefinition = new RootBeanDefinition();
                    //确定beanName
                    String beanName = component.value().equals("") ? StringUtils.getAlias(beanClass.getSimpleName()) : component.value();
                    scope = (Scope) beanClass.getAnnotation(Scope.class);
                    //设置scope，若没有设置就默认为单例
                    if (scope != null) {
                        beanDefinition.setScope(scope.value());
                    }
                    //设定对应的Resource对象
                    beanDefinition.setResource(resource);
                    //保存bean的Class对象
                    beanDefinition.setBeanClass(beanClass);
                    //添加description
                    beanDefinition.setDescription(beanName + ":" + beanClass.getName());
                    Field[] fields = beanClass.getDeclaredFields();
                    if (fields != null) {
                        for (Field field : fields) {
                            autowired = field.getAnnotation(Autowired.class);
                            //若存在依赖，则添加依赖项
                            if (autowired != null) {
                                if (dependSet == null) {
                                    dependSet = new HashSet<>(16);
                                }
                                dependSet.add(field.getName() + ";" + autowired.value());
                            }
                            qualifier = field.getAnnotation(Qualifier.class);
                            if (qualifier != null) {
                                if (propertyValues == null) {
                                    propertyValues = new MutablePropertyValues();
                                }
                                propertyName = field.getName();
                                propertyValue = new PropertyValue(propertyName, ObjectUtils.instantiateProperty(beanClass, propertyName, qualifier.value()));
                                //将属性存入propertyValues
                                propertyValues.addPropertyValue(propertyValue);
                            }
                        }
                    }
                    //添加依赖项
                    beanDefinition.setDependsOn(StringUtils.toStringArray(dependSet));
                    //添加属性项
                    beanDefinition.setPropertyValues(propertyValues);
                    beanDefinitions.put(beanName, beanDefinition);
                }
            }
        } catch (Exception n) {
            throw new BeanDefinitionStoreException("扫描包：" + packageName + "时，没有发现class文件");
        }
    }

    /**
     * 解析一个bean节点,返回对应的BeanDefinition
     *
     * @param bean
     * @param beanName
     * @param resource
     * @return
     */
    private static AbstractBeanDefinition doParse(Element bean, String beanName, Resource resource) throws BeanDefinitionStoreException {
        Attribute attribute = bean.attribute("class");
        Assert.notNull(attribute, "bean节点的class不能为空");
        String classPath = attribute.getValue();
        AbstractBeanDefinition beanDefinition;
        //用于获取bean的作用域scope
        String scope;
        //用于获取bean的Class对象
        Class<?> beanClass;
        //用于获取一个bean节点的所有property子节点
        List<Element> properties;
        try {
            //根据类路径构建bean对应的Class对象
            beanClass = Class.forName(classPath);
            // 保存最初的对象
            beanDefinition = new RootBeanDefinition();
            //设置scope，若没有设置就默认为单例
            if ((attribute = bean.attribute("scope")) != null) {
                scope = attribute.getValue();
                if (Assert.isEffectiveString(scope)) {
                    beanDefinition.setScope(scope);
                }
            }
            //设定对应的Resource对象
            beanDefinition.setResource(resource);
            //保存bean的Class对象
            beanDefinition.setBeanClass(beanClass);
            //添加description
            beanDefinition.setDescription(beanName + ":" + beanClass.getName());
            //获取所有property子节点
            properties = bean.elements();
            //遍历properties,并将所有基本属性存入propertyValues中
            MutablePropertyValues propertyValues = null;
            //将所有依赖项存入dependSet中
            Set<String> dependSet = null;
            String depend;
            //基本属性
            PropertyValue propertyValue;
            for (Element property : properties) {
                if (!property.getName().equals(PROPERTY_ELEMENT)) {
                    throw new BeanDefinitionStoreException("配置了错误的（property）节点 : " + bean.getName());
                }
                //获得proerty的name
                attribute = property.attribute("name");
                Assert.notNull(attribute, beanName + ": property节点的name不能为空");
                String propertyName = attribute.getValue();
                //获得property的value
                String value = null;
                //获得property的ref
                String ref = null;
                if ((attribute = property.attribute("value")) != null) {
                    value = attribute.getValue();
                }
                if ((attribute = property.attribute("ref")) != null) {
                    ref = attribute.getValue();
                }
                //若同时出现ref和value则抛出异常
                if (Assert.isEffectiveString(value) && Assert.isEffectiveString(ref) || !Assert.isEffectiveString(value) && !Assert.isEffectiveString(ref)) {
                    throw new BeanDefinitionStoreException("ref和value不能同时为空或者同时有值:  " + beanName + "." + propertyName);
                }
                //判断是基本属性还是引用属性的注入
                if (Assert.isEffectiveString(value)) {
                    if (propertyValues == null) {
                        propertyValues = new MutablePropertyValues();
                    }
                    try {
                        //实例化对应基本类型的value
                        propertyValue = new PropertyValue(propertyName, ObjectUtils.instantiateProperty(beanClass, propertyName, value));
                        propertyValues.addPropertyValue(propertyValue);
                    } catch (Exception e) {
                        throw new BeanDefinitionStoreException("实例化对应的字段属性:  " + beanName + "." + propertyName + " 时出错");
                    }
                } else {
                    if (dependSet == null) {
                        dependSet = new HashSet<>(16);
                    }
                    //依赖
                    depend = propertyName + ";" + ref;
                    dependSet.add(depend);
                }
            }
            //添加依赖项
            beanDefinition.setDependsOn(StringUtils.toStringArray(dependSet));
            //添加基本属性信息
            beanDefinition.setPropertyValues(propertyValues);
        } catch (ClassNotFoundException e) {
            throw new ClassCastException("没有找到Class对象 ： " + classPath);
        }
        return beanDefinition;
    }

    public static BeanDefinition getBeanDefinition(String name) {
        return beanDefinitions.get(name);
    }
}
