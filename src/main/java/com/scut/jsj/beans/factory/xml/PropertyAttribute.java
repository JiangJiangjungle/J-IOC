package com.scut.jsj.beans.factory.xml;

import lombok.Data;

@Data
public class PropertyAttribute {
    private String beanName;
    private String propertyName;
    private String valueOrRef;
    private boolean refTag;

    public PropertyAttribute(String beanName, String propertyName, String valueOrRef, boolean refTag) {
        this.beanName = beanName;
        this.propertyName = propertyName;
        this.valueOrRef = valueOrRef;
        this.refTag = refTag;
    }
}
