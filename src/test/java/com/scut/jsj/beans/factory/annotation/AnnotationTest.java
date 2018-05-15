package com.scut.jsj.beans.factory.annotation;

import com.scut.jsj.util.StringUtils;

import java.lang.reflect.Field;

@HelloAnnotation()
public class AnnotationTest {

    @Qualifier(value = "lala")
    private String name;
    @Qualifier(value = "mama")
    private String wife;

    public static void main(String[] args) throws Exception {
        HelloAnnotation annotation = AnnotationTest.class.getAnnotation(HelloAnnotation.class);
        System.out.println(annotation.value());

        System.out.println(StringUtils.getAlias("AnnotationTest"));
        Qualifier fieldAnnotation;
        Field[] fields = AnnotationTest.class.getDeclaredFields();
        for (Field field : fields) {
            fieldAnnotation = field.getAnnotation(Qualifier.class);
            System.out.println(fieldAnnotation.name() + " " + fieldAnnotation.value() + " " + field.getName());
        }

    }
}
