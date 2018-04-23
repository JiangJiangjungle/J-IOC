package com.scut.jsj.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author jsj
 * @since 2018-4-10
 * 定义了得到资源的InputStream的方法
 */
public interface InputStreamSource {
    InputStream getInputStream() throws IOException;
}
