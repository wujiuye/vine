package com.wujiuye.vine.core.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器链
 *
 * @author wujiuye 2020/08/28
 */
public class ClassFilterChain implements ClassFilter {

    private List<ClassFilter> filters;

    public ClassFilterChain() {
        this.filters = new ArrayList<>();
    }

    public ClassFilterChain addLast(ClassFilter classFilter) {
        this.filters.add(classFilter);
        return this;
    }

    @Override
    public boolean filter(String className) {
        for (ClassFilter filter : filters) {
            if (filter.filter(className)) {
                return true;
            }
        }
        return false;
    }

}
