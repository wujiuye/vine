package com.wujiuye.vine.core.aspect;

import com.wujiuye.vine.core.context.CallLinkContext;
import com.wujiuye.vine.core.context.SamplingRateContext;
import com.wujiuye.vine.spy.Spy;

import java.util.ArrayList;
import java.util.List;

/**
 * 切面
 *
 * @author wujiuye 2020/08/28
 */
public final class MethodCallListener {

    private static List<Aspect> aspects;

    private static void addAspect() {
        aspects = new ArrayList<>();
        aspects.add(new CallLinkAspect());
        aspects.add(new LogAspect());
    }

    public static void init() throws NoSuchMethodException {
        addAspect();
        Spy.beforMethod = MethodCallListener.class.getMethod("before", String.class,
                String.class, String.class, Object[].class);
        Spy.completeMethod = MethodCallListener.class.getMethod("complete", Object.class,
                String.class, String.class, String.class);
    }

    public static void before(String className, String methodName, String descriptor, Object[] params) {
        if (SamplingRateContext.needSampling()) {
            try {
                for (Aspect aspect : aspects) {
                    aspect.before(className, methodName, descriptor, params);
                }
            } catch (Exception ex) {
                // 忽略
            }
        }
    }

    public static void complete(Object returnValueOrThrowable, String className, String methodName, String descriptor) {
        try {
            if (SamplingRateContext.needSampling()) {
                if (returnValueOrThrowable instanceof Throwable) {
                    for (Aspect aspect : aspects) {
                        aspect.error(className, methodName, descriptor, (Throwable) returnValueOrThrowable);
                    }
                } else {
                    for (Aspect aspect : aspects) {
                        aspect.after(className, methodName, descriptor, returnValueOrThrowable);
                    }
                }
            }
        } catch (Exception ex) {
            // 忽略
        } finally {
            CallLinkContext.removeCallRecord();
            CallLinkContext.clear();
        }
    }

}
