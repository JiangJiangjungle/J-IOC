package com.scut.jsj.core.io;

/**
 * @author jsj
 * ResourceLoader接口，定义了Resource加载器的规范
 */
public interface ResourceLoader {

    String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String location);
}
