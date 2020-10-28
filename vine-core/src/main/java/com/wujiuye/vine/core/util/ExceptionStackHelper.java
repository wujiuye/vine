package com.wujiuye.vine.core.util;

import java.util.Arrays;

/**
 * @author wujiuye 2020/10/28
 */
public final class ExceptionStackHelper {

    /**
     * 异常信息
     *
     * @param e 异常
     */
    public static String getExceptionStackInfo(Throwable e, int limit) {
        StackTraceElement[] elements = e.getStackTrace();
        StringBuilder builder = new StringBuilder();
        builder.append(e.getClass().getName()).append(":").append(e.getMessage());
        Arrays.stream(elements)
                .limit(limit > 0 ? limit : Integer.MAX_VALUE)
                .forEach(element ->
                        builder.append("class name:").append(element.getClassName())
                                .append("method name:").append(element.getMethodName())
                                .append("line number:").append(element.getLineNumber())
                                .append("\n")
                );
        return builder.toString();
    }

}
