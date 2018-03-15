package com.scut.jsj;

public class Test {

    @org.junit.Test
    public void test() {
        String name = "xxx";
        testType(name);
    }


    public void testType(Object object) {

        String s = "lala";
        System.out.println(s.getClass()==object.getClass());
    }
}
