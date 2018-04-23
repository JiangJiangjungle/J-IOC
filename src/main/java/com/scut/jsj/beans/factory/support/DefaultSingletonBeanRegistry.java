package com.scut.jsj.beans.factory.support;

import com.scut.jsj.beans.factory.ObjectFactory;
import com.scut.jsj.beans.factory.config.SingletonBeanRegistry;
import com.scut.jsj.exception.BeansException;
import com.scut.jsj.util.Assert;
import com.scut.jsj.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的实现SingletonBeanRegistry接口的类，同时（继承类SimpleAliasRegistry，暂不实现）
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    protected static final Object NULL_OBJECT = new Object();
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    //用于缓存已经注册的单例bean名称
    private final Set<String> registeredSingletons = new LinkedHashSet(256);
    //单例bean工厂
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    private final Map<String, Object> earlySingletonObjects = new HashMap(16);
    //    //本项目用来记录bean中依赖的已经创建好的一次性bean名称
//    private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap(16);
    //记录的是依赖项所服务的所有dependentBean
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap(64);
    //记录的是dependentBean以及其所需要的依赖项
    private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
    //缓存一次性bean,如：单例dependentBean中对多例bean的依赖，这里的多例bean就属于一次性bean
//    private final Map<String, Object> disposableBeans = new LinkedHashMap();

    public DefaultSingletonBeanRegistry() {
    }

    /**
     * 将dependentBeanName和其所依赖的项beanName添加到记录中
     *
     * @param beanName
     * @param dependentBeanName
     */
    public void registerDependentBean(String beanName, String dependentBeanName) {
        //获得依赖项beanName所服务的所有bean
        Set<String> dependentBeans = (Set) this.dependentBeanMap.get(beanName);
        synchronized (this.dependentBeanMap) {
            if (dependentBeans == null || !dependentBeans.contains(dependentBeanName)) {
                if (dependentBeans == null) {
                    dependentBeans = new LinkedHashSet(8);
                    this.dependentBeanMap.put(beanName, dependentBeans);
                }
                ((Set) dependentBeans).add(dependentBeanName);
            }
        }
        synchronized (this.dependenciesForBeanMap) {
            Set<String> dependenciesForBean = (Set) this.dependenciesForBeanMap.get(dependentBeanName);
            if (dependenciesForBean == null) {
                dependenciesForBean = new LinkedHashSet(8);
                this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
            }
            ((Set) dependenciesForBean).add(beanName);
        }
    }

    /**
     * 销毁单例的bean及与其具有依赖关系的所有bean 的记录！！！
     *
     * @param beanName
     */
    public void destroySingleton(String beanName) {
        //消除bean自身
        this.removeSingleton(beanName);
//        DisposableBean disposableBean;
//        synchronized (this.disposableBeans) {
//            disposableBean = (DisposableBean) this.disposableBeans.remove(beanName);
//        }
        //消除与其相关的bean
        this.destroyBean(beanName);
    }

    /**
     * 移除单例bean的各种记录
     *
     * @param beanName
     */
    protected void removeSingleton(String beanName) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.remove(beanName);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.remove(beanName);
        }
    }

    /**
     * 销毁bean及与其具有依赖关系的一次性bean
     *
     * @param beanName
     */
    protected void destroyBean(String beanName) {
        Set<String> dependencies = (Set) this.dependentBeanMap.remove(beanName);
        //销毁该bean作为依赖项所服务的其他所有bean的记录
        if (dependencies != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
            }
            Iterator var4 = dependencies.iterator();
            while (var4.hasNext()) {
                String dependentBeanName = (String) var4.next();
                this.destroySingleton(dependentBeanName);
            }
        }
//        //此处不是很清楚
//        if (bean != null) {
//            try {
//                bean.destroy();
//            } catch (Throwable var10) {
//                this.logger.error("Destroy method on bean with name '" + beanName + "' threw an exception", var10);
//            }
//        }
//        //此处不是很清楚
//        Set<String> containedBeans = (Set) this.containedBeanMap.remove(beanName);
//        if (containedBeans != null) {
//            Iterator var13 = containedBeans.iterator();
//
//            while (var13.hasNext()) {
//                String containedBeanName = (String) var13.next();
//                this.destroySingleton(containedBeanName);
//            }
//        }
        //取消其他bean作为依赖项对该bean提供服务的记录
        synchronized (this.dependentBeanMap) {
            Iterator it = this.dependentBeanMap.entrySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Map.Entry<String, Set<String>> entry = (Map.Entry) it.next();
                Set<String> dependenciesToClean = (Set) entry.getValue();
                dependenciesToClean.remove(beanName);
                if (dependenciesToClean.isEmpty()) {
                    it.remove();
                }
            }
        }
        //删除该bean在dependenciesForBeanMap中的记录
        this.dependenciesForBeanMap.remove(beanName);
    }

    /**
     * 判断dependentBeanName是否直接或间接依赖beanName
     *
     * @param beanName
     * @param dependentBeanName
     * @return
     */
    protected boolean isDependent(String beanName, String dependentBeanName) {
        return this.isDependent(beanName, dependentBeanName, (Set) null);
    }

    /**
     * 判断dependentBeanName是否（直接或者间接）依赖beanName
     *
     * @param beanName
     * @param dependentBeanName
     * @return
     */
    private boolean isDependent(String beanName, String dependentBeanName, Set<String> alreadySeen) {
        if (alreadySeen != null && ((Set) alreadySeen).contains(beanName)) {
            return false;
        } else {
            //获得依赖项beanName所服务的所有bean
            Set<String> dependentBeans = (Set) this.dependentBeanMap.get(beanName);
            if (dependentBeans == null) {
                //若没有依赖则返回false
                return false;
            } else if (dependentBeans.contains(dependentBeanName)) {
                //若直接包含则返回true
                return true;
            } else {
                //查看是否存在间接依赖
                Iterator iterator = dependentBeans.iterator();
                String transitiveDependency;
                do {
                    if (!iterator.hasNext()) {
                        return false;
                    }
                    transitiveDependency = (String) iterator.next();
                    if (alreadySeen == null) {
                        alreadySeen = new HashSet();
                    }

                    ((Set) alreadySeen).add(beanName);
                } while (!this.isDependent(transitiveDependency, dependentBeanName, (Set) alreadySeen));
                return true;
            }
        }
    }

    /**
     * 将这个单例的singletonObject添加到registeredSingletons单例注册表中,并将对应beanName从
     * singletonFactories和earlySingletonObjects中移除
     *
     * @param beanName
     * @param singletonObject
     */
    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject != null ? singletonObject : NULL_OBJECT);
            this.singletonFactories.remove(beanName);
            this.earlySingletonObjects.remove(beanName);
            this.registeredSingletons.add(beanName);
        }
    }

    /**
     * 获得单例bean，若没有创建就进行创建并获取
     *
     * @param beanName
     * @param allowEarlyReference
     * @return
     */
    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = this.singletonObjects.get(beanName);
        //若singletonObject还没有创建，就通过对应factory进行创建
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                singletonObject = this.earlySingletonObjects.get(beanName);
                if (singletonObject == null && allowEarlyReference) {
                    ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        try {
                            singletonObject = singletonFactory.getObject();
                        } catch (BeansException e) {
                            e.fillInStackTrace();
                        }
                        this.earlySingletonObjects.put(beanName, singletonObject);
                        this.singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject != NULL_OBJECT ? singletonObject : null;
    }

    /**
     * 利用singletonFactory生成bean
     *
     * @param beanName
     * @param singletonFactory
     * @return
     */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            //先从单例池中找单例对象
            Object singletonObject = this.singletonObjects.get(beanName);
            //若找不到已经创建的单例对象，则进行创建
            if (singletonObject == null) {

//                if (this.singletonsCurrentlyInDestruction) {
//                    throw new BeanCreationNotAllowedException(beanName, "Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
//                }
//
//                if (this.logger.isDebugEnabled()) {
//                    this.logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
//                }
                //忽略检查
//                this.beforeSingletonCreation(beanName);
                boolean newSingleton = false;

                try {
                    singletonObject = singletonFactory.getObject();
                    newSingleton = true;
                } catch (IllegalStateException var16) {
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw var16;
                    }
                } catch (BeansException e) {
                    e.fillInStackTrace();
                }
                if (newSingleton) {
                    this.addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject != NULL_OBJECT ? singletonObject : null;
        }
    }

    public String[] getDependentBeans(String beanName) {
        Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
        return dependentBeans == null ? new String[0] : StringUtils.toStringArray(dependentBeans);
    }

    public String[] getDependenciesForBean(String beanName) {
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        return dependenciesForBean == null ? new String[0] : (String[]) dependenciesForBean.toArray(new String[dependenciesForBean.size()]);
    }

    public boolean isDependencyForBean(String depend, String beanName) {
        Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
        return dependenciesForBean != null && dependenciesForBean.contains(depend);
    }

    /**
     * 根据name判断是否包含单例bean
     *
     * @param beanName
     * @return
     */
    public boolean containsSingleton(String beanName) {
        return this.singletonObjects.containsKey(beanName);
    }

    /**
     * 实现了注册单例bean的方法
     *
     * @param beanName
     * @param singletonObject
     */
    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        Assert.notNull(beanName, "'beanName' must not be null");
        synchronized (this.singletonObjects) {
            Object oldObject = this.singletonObjects.get(beanName);
            if (oldObject != null) {
                throw new IllegalStateException("Could not register object : [" + singletonObject + "] under bean name '" + beanName + "': 因为已经注册过了");
            } else {
                //添加到注册表registeredSingletons中
                this.addSingleton(beanName, singletonObject);
            }
        }
    }

    @Override
    public Object getSingleton(String beanName) {
        return this.getSingleton(beanName, false);
    }

    @Override
    public String[] getSingletonNames() {
        synchronized (this.singletonObjects) {
            return StringUtils.toStringArray(this.registeredSingletons);
        }
    }

    @Override
    public int getSingletonCount() {
        return this.singletonObjects.size();
    }
}
