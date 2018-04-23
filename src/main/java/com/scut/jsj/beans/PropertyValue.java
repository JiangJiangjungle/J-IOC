package com.scut.jsj.beans;

import com.scut.jsj.util.Assert;
import com.scut.jsj.util.ObjectUtils;

import java.io.Serializable;

public class PropertyValue implements Serializable {
    private final String name;
    private final Object value;


    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public PropertyValue(PropertyValue original) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = original.getValue();
    }

    public PropertyValue(PropertyValue original, String newValue) {
        Assert.notNull(original, "Original must not be null");
        this.name = original.getName();
        this.value = newValue;
    }

    public String getName() {
        return this.name;
    }

    public Object getValue() {
        return this.value;
    }

    public int hashCode() {
        return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
    }

    public String toString() {
        return "bean property '" + this.name + "'" + " value: " + this.value;
    }
}
