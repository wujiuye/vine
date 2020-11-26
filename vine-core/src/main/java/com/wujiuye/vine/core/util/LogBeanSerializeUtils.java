package com.wujiuye.vine.core.util;

/**
 * 使用toString方法打印参数和返回值，所以要求参数和返回值重写toString方法
 *
 * @author wujiuye 2020/10/28
 */
public final class LogBeanSerializeUtils {

    public static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (ReflectionUtils.isPrimitive(obj)) {
            return obj.toString();
        }
        // 方法有多个参数，所以是数组
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            String[] newArray = new String[array.length];
            for (int i = 0; i < array.length; i++) {
                newArray[i] = array[i].toString();
            }
            return SerializeUtils.serialize(newArray);
        } else {
            return obj.toString();
        }
    }

}
