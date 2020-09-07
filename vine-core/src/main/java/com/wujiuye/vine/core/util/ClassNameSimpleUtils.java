package com.wujiuye.vine.core.util;

/**
 * @author wujiuye 2020/09/07
 */
public class ClassNameSimpleUtils {

    /**
     * 简化classname
     *
     * @param className 类名
     * @return
     */
    public static String simpleClassName(String className) {
        int index = className.lastIndexOf(".");
        if (index <= 0) {
            return className;
        }
        String name = className.substring(index + 1);
        String[] pkgs = className.substring(0, index).split("\\.");
        StringBuilder builder = new StringBuilder();
        for (String pkg : pkgs) {
            builder.append(pkg.charAt(0));
            builder.append(".");
        }
        builder.append(name);
        return builder.toString();
    }

}
