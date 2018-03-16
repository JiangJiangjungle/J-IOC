package com.scut.jsj.carrier;

import lombok.Data;

/**
 * 对应XML配置文件里的property标签的属性信息载体
 */
@Data
public class PropertyValue {

    private String name;
    private String value;
    private boolean isReference;

}
