package com.scut.jsj.core.io;

import com.scut.jsj.util.Assert;

/**
 * @author jsj
 * 默认的Resource加载实现类
 */
public class DefaultResourceLoader implements ResourceLoader {

    /**
     * 默认获取资源的方式：Spring官方返回的是一个ClassPathContextResource，在这里，先统一使用
     * 从文件系统中获取Resource的方式
     *
     * @param location
     * @return
     */
    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "location must not be null");

        //如果以“classpath:”开头，则尝试创建ClassPathResource(暂时不实现)
        if (location.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) return null;

        return new FileSystemResource(location);
    }
}
