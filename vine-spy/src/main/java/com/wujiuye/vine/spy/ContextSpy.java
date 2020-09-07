package com.wujiuye.vine.spy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 框架适配的埋点 (注意：不支持webflux等响应式框架的适配)
 *
 * @author wujiuye 2020/09/04
 */
public final class ContextSpy {

    public static Method CONTEXT_CUR_TRANSACTION_ID = null;

    public static Method CONTEXT_ENTRY = null;
    public static Method CONTEXT_EXIT = null;

    private final static ThreadLocal<String> STRING_THREAD_LOCAL = new ThreadLocal<>();

    static {
        System.out.println("ContextSpy class loader is " + Spy.class.getClassLoader());
    }

    public static String getCurTransactionId() {
        String tid = STRING_THREAD_LOCAL.get();
        if (tid == null) {
            try {
                tid = (String) CONTEXT_CUR_TRANSACTION_ID.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                //
            }
        }
        return tid;
    }

    /**
     * 由字节码调用
     *
     * @param transactionId 请求同步过来的点击ID
     */
    public static void applyCurTransactionId(String transactionId) {
        try {
            if (transactionId == null || transactionId.trim().length() == 0) {
                // 上游没有同步过来，则自己生成一个
                STRING_THREAD_LOCAL.set(UUID.randomUUID().toString());
                return;
            }
            STRING_THREAD_LOCAL.set(transactionId);
        } finally {
            if (CONTEXT_ENTRY != null) {
                try {
                    CONTEXT_ENTRY.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    //
                }
            }
        }
    }

    /**
     * 由字节码调用
     */
    public static void clearSetting() {
        STRING_THREAD_LOCAL.remove();
        if (CONTEXT_EXIT != null) {
            try {
                CONTEXT_EXIT.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                //
            }
        }
    }

}
