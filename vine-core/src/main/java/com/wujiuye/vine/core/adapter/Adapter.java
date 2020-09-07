package com.wujiuye.vine.core.adapter;

/**
 * 第三方框架适配器
 *
 * @author wujiuye 2020/09/04
 */
public interface Adapter {

    String TID_PARAM_NAME = "S-Tid";

    /**
     * 修改类
     *
     * @param loader     类加载器
     * @param className  类名
     * @param classBytes 类的字节码
     * @return
     */
    byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes);

}
