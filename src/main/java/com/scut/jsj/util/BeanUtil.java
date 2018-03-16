package com.scut.jsj.util;

import java.lang.reflect.Field;

/**
 * bean属性注入的工具类
 */
public class BeanUtil {

    /**
     * 利用反射设置对象的字段值
     *
     * @param targetObject 目标对象
     * @param feildName    字段名称
     * @param feildValue   字段值
     */
    public static void setField(Object targetObject, String feildName, String feildValue) {
        try {
            //获得对应字段
            Field field = targetObject.getClass().getDeclaredField(feildName);
            //释放权限，设为可见
            field.setAccessible(true);
            //获取字段类型
            Class type = field.getType();
            //若为基本类型或者包装类，需要转换
            if (type == int.class || type == Integer.class) {
                field.set(targetObject, Integer.parseInt(feildValue));
            } else if (type == double.class || type == Double.class) {
                field.set(targetObject, Double.parseDouble(feildValue));
            } else {
                field.set(targetObject, feildValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(targetObject.getClass().getName() + "类实例对象，设置字段:" + feildName + "失败！");
        }
    }

    /**
     * /**
     * 利用反射设置对象的(引用)字段值
     *
     * @param targetObject 目标对象
     * @param feildName    字段名称
     * @param feildValue   字段引用
     */
    public static void setField(Object targetObject, String feildName, Object feildValue) {
        try {
            //获得对应字段
            Field field = targetObject.getClass().getDeclaredField(feildName);
            //释放权限，设为可见
            field.setAccessible(true);
            field.set(targetObject, feildValue);
        } catch (Exception e) {
            throw new RuntimeException(targetObject.getClass().getName() + "类实例对象，设置字段:" + feildName + "失败！");
        }
    }

}
