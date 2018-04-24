package com.scut.jsj.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jsj
 * 对Resource的模板实现(抽象)类
 */
public abstract class AbstractResource implements Resource {

    @Override
    public boolean exists() {
        try {
            return this.getFile().exists();
        } catch (IOException e) {
            try {
                InputStream is = this.getInputStream();
                is.close();
                return true;
            } catch (Throwable throwable) {
                return false;
            }
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(this.getDescription() + " cannot be resolved to absolute file path");
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}
