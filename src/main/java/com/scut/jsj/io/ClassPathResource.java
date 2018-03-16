package com.scut.jsj.io;

import lombok.Data;

@Data
public class ClassPathResource implements Resource {

    private String path;

    public ClassPathResource(String path) {
        if (path == null || !path.endsWith(".xml")) {
            throw new RuntimeException("配置文件PATH不合法！");
        }
        this.path = path;
    }

    @Override
    public String getFileName() {
        if (path == null) {
            return null;
        } else {
            int separatorIndex = path.lastIndexOf("/");
            return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
        }
    }

    @Override
    public boolean isReadable() {
        return true;
    }
}
