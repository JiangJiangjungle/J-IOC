package com.scut.jsj.util;

import java.io.File;

public class ClassUtils {

    public static String[] getPackageAllClassName(String classLocation, String packageName) {
        File packeageDir = new File(classLocation + packageName.replaceAll("[.]", File.separator));
        String[] packageNames = packeageDir.list();
        if (packageNames != null) {
            for (int i = 0; i < packageNames.length; i++) {
                packageNames[i] = packageNames[i].substring(0, packageNames[i].indexOf(".class"));
            }
            return packageNames;
        }
        return null;
    }
}
