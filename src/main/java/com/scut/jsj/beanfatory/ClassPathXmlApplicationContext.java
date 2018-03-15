package com.scut.jsj.beanfatory;

import com.scut.jsj.conf.Bean;
import com.scut.jsj.conf.Property;
import com.scut.jsj.conf.resolve.ConfigurationManager;
import com.scut.jsj.util.BeanUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ClassPathXmlApplicationContext implements BeanFactory {

    //存放配置文件信息
    private Map<String, Bean> config;
    //存放需要new的单例bean对象的容器
    private Map<String, Object> context = new HashMap<>();

    /**
     * 将配置文件中设置为单例的对象创建出来并放入容器中
     *
     * @param path
     */
    public ClassPathXmlApplicationContext(String path) {
        if (path == null || !path.endsWith(".xml")) {
            throw new RuntimeException("配置文件PATH不合法！");
        }
        //读取配置文件中的bean节点信息
        config = ConfigurationManager.getBeanConfig(path);
        //根据配置文件创建单例对象
        createBeansAndSetValueAndReference(config);
    }

    /**
     * 根据配置文件创建所有单例对象
     *
     * @param config
     */
    private void createBeansAndSetValueAndReference(Map<String, Bean> config) {
        if (config == null) {
            return;
        }
        //遍历初始化bean
        String name;
        Bean beanConfig;
        Object object;
        //先创建默认对象并对字段赋值（不包括引用）
        for (Map.Entry<String, Bean> entry : config.entrySet()) {
            name = entry.getKey();
            beanConfig = entry.getValue();
            //如果scope是SINGLETON，就创建单例对象并存入context容器中
            if (beanConfig.getScope().equals(Bean.SINGLETON)) {
                //创建对象
                object = createBeanAndSetValue(beanConfig);
                //存入context容器
                context.put(name, object);
            }
        }
        //对引用字段赋值
        for (Map.Entry<String, Bean> entry : config.entrySet()) {
            name = entry.getKey();
            beanConfig = entry.getValue();
            //如果scope是SINGLETON，就对引用字段赋值并存入context容器中
            if (beanConfig.getScope().equals(Bean.SINGLETON)) {
                //设置引用字段
                object = linkBeanReference(context.get(name), beanConfig);
                //重新存入context容器
                context.put(name, object);
            }
        }
    }

    /**
     * 根据配置对bean的引用字段进行赋值
     *
     * @param object
     * @param beanConfig
     * @return
     */
    private Object linkBeanReference(Object object, Bean beanConfig) {
        try {
            //获取bean节点中各property配置
            List<Property> properties = beanConfig.getProperties();
            //引用字段名称
            String fieldName;
            //字段所引用的对象在配置文件中的beanID
            String beanID;
            //根据beanID获取该对象
            Object referenceObject;
            //引用对象的配置信息
            Bean refBean;
            //进行遍历，查找并设置引用字段
            for (Property property : properties) {
                fieldName = property.getName();
                beanID = property.getRef();
                if (beanID != null) {
                    refBean = config.get(beanID);
                    if (refBean.getScope().equals(Bean.SINGLETON)) {
                        referenceObject = context.get(beanID);
                    } else {
                        referenceObject = this.createBeanAndSetValue(refBean);
                        referenceObject = this.linkBeanReference(referenceObject, refBean);
                    }
                    BeanUtil.setField(object, fieldName, referenceObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("对" + beanConfig.getClassName() + "设置引用字段失败！");
        }
        return object;

    }

    /**
     * 根据配置文件中bean节点信息创建单例对象，并且对字段赋值（不包括引用）
     *
     * @param beanConfig
     * @return
     */
    private Object createBeanAndSetValue(Bean beanConfig) {
        //根据bean节点信息创建bean对象
        Class clazz = null;
        Object object = null;
        try {
            //获得Class对象
            clazz = Class.forName(beanConfig.getClassName());
            //创建bean默认构造器对象
            object = clazz.newInstance();
            //获取bean节点中各property配置
            List<Property> properties = beanConfig.getProperties();
            //字段名称
            String fieldName;
            //字段值
            String fieldValue;
            //进行遍历，设置各个字段值
            for (Property property : properties) {
                fieldName = property.getName();
                fieldValue = property.getValue();
                if (fieldValue != null) {
                    BeanUtil.setField(object, fieldName, fieldValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建" + beanConfig.getClassName() + "对象并设置字段值失败！");
        }

        return object;
    }

    @Override
    public Object getBean(String name) {
        Bean beanConfig = config.get(name);
        if (beanConfig == null) {
            throw new RuntimeException("xml配置文件中没有 bean: " + name + " 的配置信息");
        }
        switch (beanConfig.getScope()) {
            case Bean.SINGLETON:
                return context.get(name);
            case Bean.PROTOTYPE:
                Object object = this.createBeanAndSetValue(beanConfig);
                return this.linkBeanReference(object, beanConfig);
            default:
                return null;
        }
    }
}
