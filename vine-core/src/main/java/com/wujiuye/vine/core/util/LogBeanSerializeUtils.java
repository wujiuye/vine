package com.wujiuye.vine.core.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wujiuye 2020/10/28
 */
public final class LogBeanSerializeUtils {

    private final static Set<String> BEAN_CLASS_NAME;

    static {
        BEAN_CLASS_NAME = new HashSet<>();
        BEAN_CLASS_NAME.add("Dto");
        BEAN_CLASS_NAME.add("Command");
        BEAN_CLASS_NAME.add("Query");
        BEAN_CLASS_NAME.add("Entity");
        BEAN_CLASS_NAME.add("Config");
        BEAN_CLASS_NAME.add("Props");
    }

    public static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (ReflectionUtils.isPrimitive(obj)) {
            return obj.toString();
        }
        StringBuilder builder = new StringBuilder();
        // 方法有多个参数，所以是数组
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            Object[] newArray = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                if (!supporSerialize(array[i])) {
                    newArray[i] = "serializable error";
                    continue;
                }
                newArray[i] = array[i];
            }
            String result = SerializeUtils.serialize(newArray);
            if (result != null) {
                builder.append(result);
            }
        } else {
            if (!supporSerialize(obj)) {
                builder.append("serializable error");
            } else {
                String result = SerializeUtils.serialize(obj);
                if (result != null) {
                    builder.append(result);
                }
            }
        }
        return builder.toString();
    }

    private static boolean supporSerialize(Object object) {
        if (object == null) {
            return true;
        }
        if (ReflectionUtils.isPrimitive(object)) {
            return true;
        }
        if (object.getClass().isAnnotation()) {
            return false;
        }
        // 方法的某个参数是数组
        if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            if (array[0] != null) {
                Object item = array[0];
                return supporSerialize(item);
            }
        }
        // 方法的某个参数是集合
        if (object instanceof Collection) {
            for (Object item : (Collection<?>) object) {
                return supporSerialize(item);
            }
        }
        // 过滤jdk类
        String className = object.getClass().getName().toLowerCase();
        if (className.startsWith("java.") || className.startsWith("sun.")) {
            return false;
        }
        // 过滤spring、jackjson类
        if (className.startsWith("org.springframework") || className.startsWith("com.fasterxml")) {
            return false;
        }
        // 只序列化某些类
        for (String name : BEAN_CLASS_NAME) {
            if (className.endsWith(name)) {
                return true;
            }
        }
        return false;
    }

}
