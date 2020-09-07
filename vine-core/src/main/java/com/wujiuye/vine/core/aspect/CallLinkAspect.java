package com.wujiuye.vine.core.aspect;

import com.wujiuye.vine.core.config.GlobalConfigManager;
import com.wujiuye.vine.core.context.CallLinkContext;
import com.wujiuye.vine.core.context.CallRecord;
import com.wujiuye.vine.core.util.ClassNameSimpleUtils;

/**
 * 记录调用链切面
 *
 * @author wujiuye 2020/08/28
 */
public final class CallLinkAspect implements Aspect {

    @Override
    public void before(String className, String methodName, String descriptor, Object[] params) {
        CallRecord callRecord = new CallRecord();
        callRecord.setClassName(ClassNameSimpleUtils.simpleClassName(className));
        callRecord.setMethodName(methodName);
        callRecord.setDescriptor(GlobalConfigManager.getConfig().isLogShowMethodDescriptor() ? descriptor : null);
        callRecord.setParams(params);
        callRecord.setStartTime(System.currentTimeMillis());
        CallLinkContext.putCallRecord(callRecord);
    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {
        CallLinkContext.getOrCreateContext().setExistError(true);
        CallRecord callRecord = CallLinkContext.getCurCallRecord();
        if (callRecord != null) {
            callRecord.setThrowable(throwable);
            callRecord.setFinishTime(System.currentTimeMillis());
        }
    }

    @Override
    public void after(String className, String methodName, String descriptor, Object returnValue) {
        CallRecord callRecord = CallLinkContext.getCurCallRecord();
        if (callRecord != null) {
            callRecord.setReturnValue(returnValue);
            callRecord.setFinishTime(System.currentTimeMillis());
        }
    }

}
