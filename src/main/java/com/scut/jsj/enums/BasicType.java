package com.scut.jsj.enums;

/**
 * @author jsj
 * @since 2018-4-18
 * 这里列出8种基本类型及其包装类的名称
 */
public enum BasicType {

    Boolean("boolean", "Boolean"),
    Character("char", "Character"),
    Integer("int", "Integer"),
    Byte("byte", "Byte"),
    Short("short", "Short"),
    Long("long", "Long"),
    Float("float", "Float"),
    Double("double", "Double");

    public String simpleTypeName;
    public String wrappedTypeName;

    BasicType(java.lang.String simpleTypeName, java.lang.String wrappedTypeName) {
        this.simpleTypeName = simpleTypeName;
        this.wrappedTypeName = wrappedTypeName;
    }
}
