package com.scut.jsj.util;

import java.util.Collection;

/**
 * 一个字符串的静态工具类
 */
public class StringUtils {

    public static String[] toStringArray(Collection<String> collection) {
        return collection == null ? null : collection.toArray(new String[collection.size()]);
    }

    public static boolean hasText(String str) {
        return hasLength(str) && containsText(str);
    }

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据属性名称得到对应的set方法
     *
     * @param propertyName
     * @return
     */
    public static String offerSetMethodName(String propertyName) {
        if (!Assert.isEffectiveString(propertyName)) return null;
        if (propertyName.length() == 1) return "set" + propertyName.toUpperCase();
        char ch1 = propertyName.charAt(1);
        if (64 < ch1 && ch1 < 91) {
            return "set" + propertyName;
        } else {
            return "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
    }

    public static String getDependentBeanName(String dependOn) {
        if (!Assert.isEffectiveString(dependOn)) return null;
        return dependOn.substring(dependOn.indexOf(";") + 1);
    }
}
