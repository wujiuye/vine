package com.wujiuye.vine.core.context;

/**
 * 监听器
 *
 * @author wujiuye 2020/09/07
 */
public interface ContextListener {

    /**
     * 调用链路入口监听
     */
    void entry();

    /**
     * 调用链路出口监听
     */
    void exit();

}
