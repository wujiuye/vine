package com.wujiuye.vine.core.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wujiuye 2020/09/03
 */
public class ReflectionUtils {

    private final static Set<Class<?>> PRIMITIVE_CLASS;

    static {
        PRIMITIVE_CLASS = new HashSet<>();
        PRIMITIVE_CLASS.addAll(Arrays.asList(
                int.class,
                byte.class,
                short.class,
                char.class,
                float.class,
                double.class,
                long.class,
                boolean.class,
                Integer.class,
                Byte.class,
                Short.class,
                Character.class,
                Float.class,
                Double.class,
                Long.class,
                Boolean.class,
                String.class,
                BigDecimal.class
        ));
    }

    public static boolean isPrimitive(Object object) {
        return object == null || PRIMITIVE_CLASS.contains(object.getClass());
    }

    public static boolean isPrimitive(Class<?> clz) {
        return PRIMITIVE_CLASS.contains(clz);
    }

}
