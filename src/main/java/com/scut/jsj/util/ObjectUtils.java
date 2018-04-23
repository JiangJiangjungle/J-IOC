package com.scut.jsj.util;


import com.scut.jsj.enums.BasicType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ObjectUtils {

    public static int nullSafeHashCode(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            if (obj.getClass().isArray()) {
                if (obj instanceof Object[]) {
                    return nullSafeHashCode((Object[]) ((Object[]) obj));
                }

                if (obj instanceof boolean[]) {
                    return nullSafeHashCode((boolean[]) ((boolean[]) obj));
                }

                if (obj instanceof byte[]) {
                    return nullSafeHashCode((byte[]) ((byte[]) obj));
                }

                if (obj instanceof char[]) {
                    return nullSafeHashCode((char[]) ((char[]) obj));
                }

                if (obj instanceof double[]) {
                    return nullSafeHashCode((double[]) ((double[]) obj));
                }

                if (obj instanceof float[]) {
                    return nullSafeHashCode((float[]) ((float[]) obj));
                }

                if (obj instanceof int[]) {
                    return nullSafeHashCode((int[]) ((int[]) obj));
                }

                if (obj instanceof long[]) {
                    return nullSafeHashCode((long[]) ((long[]) obj));
                }

                if (obj instanceof short[]) {
                    return nullSafeHashCode((short[]) ((short[]) obj));
                }
            }

            return obj.hashCode();
        }
    }

    public static boolean nullSafeEquals(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 != null && o2 != null) {
            if (o1.equals(o2)) {
                return true;
            } else {
                return o1.getClass().isArray() && o2.getClass().isArray() ? arrayEquals(o1, o2) : false;
            }
        } else {
            return false;
        }
    }

    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) ((Object[]) o1), (Object[]) ((Object[]) o2));
        } else if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) ((boolean[]) o1), (boolean[]) ((boolean[]) o2));
        } else if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) ((byte[]) o1), (byte[]) ((byte[]) o2));
        } else if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) ((char[]) o1), (char[]) ((char[]) o2));
        } else if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) ((double[]) o1), (double[]) ((double[]) o2));
        } else if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) ((float[]) o1), (float[]) ((float[]) o2));
        } else if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) ((int[]) o1), (int[]) ((int[]) o2));
        } else if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) ((long[]) o1), (long[]) ((long[]) o2));
        } else {
            return o1 instanceof short[] && o2 instanceof short[] ? Arrays.equals((short[]) ((short[]) o1), (short[]) ((short[]) o2)) : false;
        }
    }

    /**
     * 给bean的对应字段field进行注入
     *
     * @param fieldName    ：字段名称
     * @param beanClass    ：bean的Class对象
     * @param propertyType ：字段属性
     * @param targetObject ：bean实例
     * @param value        ：属性实例
     * @throws Exception
     */
    public static void doInvokeSetMethod(String fieldName, Class<?> beanClass, Class<?> propertyType, Object targetObject, Object value) throws Exception {
        if (!Assert.isEffectiveString(fieldName)) throw new Exception("字段名必须有效!");
        //得到字段对应的set方法名称
        String methodName = StringUtils.offerSetMethodName(fieldName);
        Method method;
        try {
            method = beanClass.getMethod(methodName, propertyType);
            method.invoke(targetObject, value);
        } catch (NoSuchMethodException e) {
            if (value instanceof Boolean) {
                propertyType = boolean.class;
            } else if (value instanceof Byte) {
                propertyType = byte.class;
            } else if (value instanceof Character) {
                propertyType = char.class;
            } else if (value instanceof Short) {
                propertyType = short.class;
            } else if (value instanceof Integer) {
                propertyType = int.class;
            } else if (value instanceof Float) {
                propertyType = float.class;
            } else if (value instanceof Long) {
                propertyType = long.class;
            } else if (value instanceof Double) {
                propertyType = double.class;
            } else {
                throw new Exception("进行依赖注入时找不到对应的属性类型");
            }
            method = beanClass.getMethod(methodName, propertyType);
            method.invoke(targetObject, value);
        }
    }

    public static Object instantiateProperty(Class<?> beanClass, String propertyName, String value) throws Exception {
        Object resultObject;
        Field field = beanClass.getDeclaredField(propertyName);
        String type = field.getType().getSimpleName();
        if (type.equals("String")) {
            resultObject = value;
        } else if (type.equals(BasicType.Boolean.simpleTypeName) || type.equals(BasicType.Boolean.wrappedTypeName)) {
            resultObject = Boolean.valueOf(value);
        } else if (type.equals(BasicType.Byte.simpleTypeName) || type.equals(BasicType.Byte.wrappedTypeName)) {
            resultObject = Byte.valueOf(value);
        } else if (type.equals(BasicType.Character.simpleTypeName) || type.equals(BasicType.Character.wrappedTypeName)) {
            resultObject = value.charAt(0);
        } else if (type.equals(BasicType.Short.simpleTypeName) || type.equals(BasicType.Short.wrappedTypeName)) {
            resultObject = Short.valueOf(value);
        } else if (type.equals(BasicType.Integer.simpleTypeName) || type.equals(BasicType.Integer.wrappedTypeName)) {
            resultObject = Integer.valueOf(value);
        } else if (type.equals(BasicType.Float.simpleTypeName) || type.equals(BasicType.Float.wrappedTypeName)) {
            resultObject = Float.valueOf(value);
        } else if (type.equals(BasicType.Long.simpleTypeName) || type.equals(BasicType.Long.wrappedTypeName)) {
            resultObject = Long.valueOf(value);
        } else if (type.equals(BasicType.Double.simpleTypeName) || type.equals(BasicType.Double.wrappedTypeName)) {
            resultObject = Double.valueOf(value);
        } else {
            throw new Exception("实例化一个基本类型（及其包装类）属性时找不到对应的类型");
        }
        return resultObject;
    }

}
