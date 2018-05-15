package com.scut.jsj.util;

public class Assert {

    public static void notNull(Object object, String message) {
        if (object == null) throw new IllegalArgumentException(message);
    }

    public static void hasText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isEffectiveString(String string) {
        return string != null && !string.equals("");
    }
}
