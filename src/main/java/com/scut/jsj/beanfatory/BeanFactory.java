package com.scut.jsj.beanfatory;

public interface BeanFactory {

    /**
     * 根据name返回bean对象
     *
     * @param name
     * @return
     */
    Object getBean(String name);
}
