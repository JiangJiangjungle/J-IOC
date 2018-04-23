package com.scut.jsj.util;

import com.scut.jsj.entity.Person;

public class Test {


    @org.junit.Test
    public void test() throws Exception {
        String classPath = "com.scut.jsj.entity.Person";
        Class<?> clazz = Class.forName(classPath);
        Person person = new Person();
        String value = "13";
        Object result = ObjectUtils.instantiateProperty(clazz, "money", value);
        if (result instanceof Double) {
            System.out.println("yeah: " + result);
        }

    }

    @org.junit.Test
    public void test2() throws Exception {
    }
}
