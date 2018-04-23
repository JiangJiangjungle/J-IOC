package com.scut.jsj.core.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jsj
 * @since 2018-4-10
 * 对File进行封装而实现的Resource，继承了AbstractResource抽象类
 * 重写了isReadable()方法和getDescription()方法
 */
public class FileSystemResource extends AbstractResource {
    private final File file;
    private final String path;

    public FileSystemResource(File file) {
        this.file = file;
        this.path = file.getAbsolutePath();
    }

    public FileSystemResource(String path) {
        this.path = path;
        this.file = new File(path);
    }

    @Override
    public String getFileName() {
        return this.file.getName();
    }

    @Override
    public boolean isReadable() {
        return this.file.canRead() && !this.file.isDirectory();
    }

    @Override
    public String getDescription() {
        return "fileName: [" + this.file.getName() + "]" + " path: [" + this.path + "]";
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public File getFile() throws IOException {
        return this.file;
    }

    public String getPath() {
        return path;
    }
}
