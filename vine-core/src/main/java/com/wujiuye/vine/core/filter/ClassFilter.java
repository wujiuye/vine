package com.wujiuye.vine.core.filter;

/**
 * 过滤器
 *
 * @author wujiuye 2020/08/28
 */
public interface ClassFilter {

    /**
     * 过期行为
     *
     * @param className 类型名称
     * @return true:过滤
     */
    boolean filter(String className);

}
