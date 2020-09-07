package com.wujiuye.vine.spy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 埋点
 *
 * @author wujiuye 2020/08/28
 */
public final class Spy {

    public static Method beforMethod;
    public static Method completeMethod;

    static {
        System.out.println("Spy class loader is " + Spy.class.getClassLoader());
    }

    public static void before(String className, String methodName, String descriptor, Object[] params) {
        if (beforMethod != null) {
            try {
                beforMethod.invoke(null, className, methodName, descriptor, params);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void complete(Object returnValueOrThrowable, String className, String methodName, String descriptor) {
        if (completeMethod != null) {
            try {
                completeMethod.invoke(null, returnValueOrThrowable, className, methodName, descriptor);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
