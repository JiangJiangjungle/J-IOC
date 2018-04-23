package com.scut.jsj.core.io;


import java.io.File;
import java.io.IOException;

/**
 * @author jsj
 * @since 2018-4-10
 * 用来描述资源（XML文件等）的类（作用类似于URL）
 */
public interface Resource extends InputStreamSource {

    String getFileName();

    boolean exists();

    boolean isReadable();

    String getDescription();

    File getFile() throws IOException;
}
