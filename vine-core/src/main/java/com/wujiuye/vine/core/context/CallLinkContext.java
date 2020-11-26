package com.wujiuye.vine.core.context;

import com.wujiuye.vine.spy.ContextSpy;

/**
 * 调用链上下文
 *
 * @author wujiuye 2020/08/28
 */
public final class CallLinkContext {

    private final static ThreadLocal<Context> CONTEXT = new ThreadLocal<>();

    static {
        try {
            ContextSpy.CONTEXT_CUR_TRANSACTION_ID = CallLinkContext.class.getMethod("getCurTransactionId");
            ListenerManager.init();
        } catch (NoSuchMethodException e) {
            //
        }
    }

    /**
     * 提供给ContextSpy调用
     *
     * @return
     */
    public static String getCurTransactionId() {
        Context context = CONTEXT.get();
        if (context == null) {
            return null;
        }
        return context.getTransactionId();
    }

    public static Context getOrCreateContext() {
        Context context = CONTEXT.get();
        if (context == null) {
            context = new Context(ContextSpy.getCurTransactionId());
            CONTEXT.set(context);
            SamplingRateContext.inc();
        }
        return context;
    }

    public static CallRecord getCurCallRecord() {
        Context context = getOrCreateContext();
        return context.getCurRecord();
    }

    public static void putCallRecord(CallRecord record) {
        Context context = getOrCreateContext();
        record.setTransactionId(context.getTransactionId());
        CallRecord cur = context.getCurRecord();
        if (cur == null) {
            context.setCurRecord(record);
            return;
        }
        cur.setNext(record);
        record.setPre(cur);
        context.setCurRecord(record);
    }

    public static void removeCallRecord() {
        Context context = getOrCreateContext();
        CallRecord cur = context.getCurRecord();
        if (cur == null) {
            return;
        }
        context.setCurRecord(cur.getPre());
    }

    public static void clear() {
        SamplingRateContext.reset();
        if (CONTEXT.get() == null) {
            return;
        }
        if (CONTEXT.get().getCurRecord() == null) {
            CONTEXT.remove();
        }
    }

}
