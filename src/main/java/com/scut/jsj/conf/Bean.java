package com.scut.jsj.conf;

import lombok.Data;

import java.util.List;

/**
 * 对应XML配置文件中bean标签的信息
 */
@Data
public class Bean {

    public static final String SINGLETON = "singleton";
    public static final String PROTOTYPE = "prototype";

    private String id;
    private String className;
    //默认bean是单例对象
    private String scope = SINGLETON;

    private List<Property> properties;

    public Bean(String id, String className) {
        this.id = id;
        this.className = className;
    }
}
