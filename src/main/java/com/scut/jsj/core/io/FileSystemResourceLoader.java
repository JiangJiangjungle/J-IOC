package com.scut.jsj.core.io;


import java.io.File;

/**
 * 继承自DefaultResourceLoader的Resource加载实现类
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

    @Override
    public Resource getResource(String location) {
        return new FileSystemResource(location);
    }

    /**
     * 利用File对象获取Resource
     *
     * @param file
     * @return
     */
    public Resource getResource(File file) {
        return new FileSystemResource(file);
    }
}
