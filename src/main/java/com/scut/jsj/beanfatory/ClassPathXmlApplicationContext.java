package com.scut.jsj.beanfatory;

import com.scut.jsj.carrier.BeanDefinition;
import com.scut.jsj.carrier.PropertyValue;
import com.scut.jsj.io.ClassPathResource;
import com.scut.jsj.resolve.BeanDefinitionReader;
import com.scut.jsj.util.BeanUtil;
import lombok.Data;

import java.util.*;

@Data
public class ClassPathXmlApplicationContext implements BeanFactory {

    //存放配置文件信息
    private Map<String, BeanDefinition> config;
    //存放需要new的单例bean对象的容器
    private Map<String, Object> context = new HashMap<>();
    //依赖注入完毕的对象名单
    private Set<String> idSet = new HashSet<>();

    /**
     * 将配置文件中设置为单例的对象创建出来并放入容器中
     *
     * @param path
     */
    public ClassPathXmlApplicationContext(String path) {
        //获得Resource对象
        ClassPathResource resource = new ClassPathResource(path);
        //读取配置文件中的bean节点信息
        config = BeanDefinitionReader.loadBeanDefinitions(resource);
        //根据配置文件创建单例对象
        createBeans(config);
    }

    /**
     * 根据配置对bean的引用字段进行赋值
     *
     * @param target
     * @param beanDefinition
     * @return
     */
    private Object linkBeanReference(Object target, BeanDefinition beanDefinition) {
        //引用字段名称
        String fieldName;
        //字段所引用的对象在配置文件中的beanID
        String beanID;
        //根据beanID获取该对象
        Object referenceObject;
        //引用对象的配置信息
        BeanDefinition refBeanDefinition;
        try {
            //获取bean节点中各property配置
            List<PropertyValue> properties = beanDefinition.getProperties();
            //进行遍历，查找并设置引用字段
            for (PropertyValue propertyValue : properties) {
                if (propertyValue.isReference()) {
                    fieldName = propertyValue.getName();
                    beanID = propertyValue.getValue();
                    if (beanID != null) {
                        refBeanDefinition = config.get(beanID);
                        if (refBeanDefinition.getScope().equals(BeanDefinition.SINGLETON)) {
                            referenceObject = context.get(beanID);
                        } else {
                            referenceObject = this.createBean(refBeanDefinition);
                            referenceObject = this.linkBeanReference(referenceObject, refBeanDefinition);
                        }
                        BeanUtil.setField(target, fieldName, referenceObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("对" + beanDefinition.getClassName() + "设置引用字段失败！");
        }
        return target;
    }

    /**
     * 根据配置文件中bean节点信息对字段赋值（不包括引用）
     *
     * @param beanDefinition
     * @return
     */
    private Object injectValue(Object target, BeanDefinition beanDefinition) {
        try {
            //获取bean节点中各property配置
            List<PropertyValue> properties = beanDefinition.getProperties();
            //字段名称
            String fieldName;
            //字段值
            String fieldValue;
            //进行遍历，设置各个字段值
            for (PropertyValue propertyValue : properties) {
                if (!propertyValue.isReference()) {
                    fieldName = propertyValue.getName();
                    fieldValue = propertyValue.getValue();
                    if (fieldValue != null) {
                        BeanUtil.setField(target, fieldName, fieldValue);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(beanDefinition.getClassName() + "设置字段值失败！");
        }
        return target;
    }

    /**
     * 利用反射机制，以默认构造器形式创建所有单例对象,存入context容器
     *
     * @param config
     */
    private void createBeans(Map<String, BeanDefinition> config) {
        //创建bean对象
        Object object;
        BeanDefinition beanDefinition = null;
        String beanID;
        try {
            for (Map.Entry<String, BeanDefinition> entry : config.entrySet()) {
                beanID = entry.getKey();
                beanDefinition = entry.getValue();
                if (beanDefinition.getScope().equals(BeanDefinition.SINGLETON)) {
                    object = createBean(beanDefinition);
                    //存入context容器
                    context.put(beanID, object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (beanDefinition != null) {
                throw new RuntimeException("创建" + beanDefinition.getClassName() + "对象失败！");
            } else {
                throw new RuntimeException("调用createBeans()方法创建对象失败！");
            }
        }
    }

    /**
     * 创建一个对象
     *
     * @param beanDefinition
     * @return
     */
    private Object createBean(BeanDefinition beanDefinition) {
        Object object;
        try {
            Class clazz = Class.forName(beanDefinition.getClassName());
            //创建bean默认构造器对象
            object = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建" + beanDefinition.getClassName() + "对象失败！");
        }
        return object;
    }

    @Override
    public Object getBean(String name) {
        Object result;
        BeanDefinition beanDefinition = config.get(name);
        if (beanDefinition == null) {
            throw new RuntimeException("xml配置文件中没有 bean: " + name + " 的配置信息");
        }
        String beanID = beanDefinition.getId();
        if (beanDefinition.getScope().equals(BeanDefinition.SINGLETON)) {
            result = context.get(beanID);
            if (idSet.contains(beanID)) {
                return result;
            }
            idSet.add(beanID);
        } else {
            result = createBean(beanDefinition);
        }
        result = injectValue(result, beanDefinition);
        return linkBeanReference(result, beanDefinition);
    }
}
