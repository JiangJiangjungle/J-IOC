package com.scut.jsj.util;

public class Test {


    @org.junit.Test
    public void test() throws Exception {
        String classPath = "com.scut.jsj.entity.Person";
        Class<?> clazz = Class.forName(classPath);
        String value = "13";
        Object result = ObjectUtils.instantiateProperty(clazz, "money", value);
        if (result instanceof Double) {
            System.out.println("yeah: " + result);
        }

    }

}
