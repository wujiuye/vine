package com.wujiuye.vine.core.util;

import java.util.Comparator;
import java.util.List;

/**
 * 判断工具类
 *
 * @author wujiuye 2020/08/28
 */
public class IncludeUtils {

    /**
     * 判断target是否至少有一个元素存在【include】中
     *
     * @param include
     * @param target
     * @param <T>
     * @return
     */
    public static <T> boolean include(List<T> include, T... target) {
        if (include == null || include.isEmpty()) {
            return false;
        }
        if (target.length == 0) {
            return false;
        }
        for (T targetObj : target) {
            for (T inObj : include) {
                if (inObj.equals(targetObj)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断target是否至少有一个元素存在【include】中
     *
     * @param include
     * @param target
     * @param comparator
     * @param <T>
     * @return
     */
    public static <T> boolean includeComparator(List<T> include, Comparator<T> comparator, T... target) {
        if (include == null || include.isEmpty()
                || comparator == null || target.length == 0) {
            return false;
        }
        for (T targetObj : target) {
            for (T inObj : include) {
                if (comparator.compare(inObj, targetObj) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
