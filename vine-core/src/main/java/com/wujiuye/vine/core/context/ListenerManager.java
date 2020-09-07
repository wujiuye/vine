package com.wujiuye.vine.core.context;

import com.wujiuye.vine.core.aspect.LogAspect;
import com.wujiuye.vine.spy.ContextSpy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wujiuye 2020/09/07
 */
public final class ListenerManager {

    private final static List<ContextListener> CONTEXT_LISTENERS;

    static {
        CONTEXT_LISTENERS = new ArrayList<>();
        CONTEXT_LISTENERS.add(new LogAspect());
    }

    public static void init() {
        try {
            ContextSpy.CONTEXT_ENTRY = ListenerManager.class.getMethod("entry");
            ContextSpy.CONTEXT_EXIT = ListenerManager.class.getMethod("exit");
        } catch (NoSuchMethodException e) {
            //
        }
    }

    /**
     * 调用链路入口监听
     */
    public static void entry() {
        for (ContextListener listener : CONTEXT_LISTENERS) {
            try {
                listener.entry();
            } catch (Throwable throwable) {
                //
            }
        }
    }

    /**
     * 调用链路出口监听
     */
    public static void exit() {
        for (ContextListener listener : CONTEXT_LISTENERS) {
            try {
                listener.exit();
            } catch (Throwable throwable) {
                //
            }
        }
    }

}
