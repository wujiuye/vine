package com.wujiuye.vine.core.aspect;

/**
 * 切面
 *
 * @author wujiuye 2020/08/28
 */
public interface Aspect {

    /**
     * 方法调用之前
     *
     * @param className
     * @param methodName
     * @param descriptor
     * @param params
     */
    void before(String className, String methodName, String descriptor, Object[] params);

    /**
     * 方法抛出异常
     *
     * @param className
     * @param methodName
     * @param descriptor
     * @param throwable
     */
    void error(String className, String methodName, String descriptor, Throwable throwable);

    /**
     * 方法调用完成
     *
     * @param className
     * @param methodName
     * @param descriptor
     * @param returnValue
     */
    void after(String className, String methodName, String descriptor, Object returnValue);

}
