package com.scut.jsj.conf;

import lombok.Data;

/**
 * 对应XML配置文件里的property标签的信息
 */
@Data
public class Property {

    private String name;
    private String value;
    private String ref;

}
