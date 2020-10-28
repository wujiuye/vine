package com.wujiuye.vine.core.aspect;

import com.wujiuye.vine.core.context.CallLinkContext;
import com.wujiuye.vine.core.context.CallRecord;
import com.wujiuye.vine.core.context.ContextListener;
import com.wujiuye.vine.core.util.*;

import java.util.*;

/**
 * 日记打印切面
 *
 * @author wujiuye 2020/08/28
 */
public class LogAspect implements Aspect, ContextListener {

    private final static ThreadLocal<LinkedList<LogRecord>> CALL_LINKE_LOG_CONTEXT = new ThreadLocal<>();

    @Override
    public void before(String className, String methodName, String descriptor, Object[] params) {
        // not print
    }

    @Override
    public void error(String className, String methodName, String descriptor, Throwable throwable) {
        prinfLog(true);
    }

    @Override
    public void after(String className, String methodName, String descriptor, Object returnValue) {
        prinfLog(CallLinkContext.getOrCreateContext().isExistError());
    }

    private void prinfLog(boolean printParamAndReturnValue) {
        CallRecord callRecord = CallLinkContext.getCurCallRecord();
        if (callRecord.getTransactionId() == null) {
            // 过滤调没有事务ID的调用链（说明不是接口调用）
            return;
        }
        LogRecord logRecord = LogRecord.toLogRecord(callRecord, printParamAndReturnValue);
        CALL_LINKE_LOG_CONTEXT.get().add(logRecord);
    }

    @Override
    public void entry() {
        CALL_LINKE_LOG_CONTEXT.set(new LinkedList<>());
    }

    @Override
    public void exit() {
        LinkedList<LogRecord> records = CALL_LINKE_LOG_CONTEXT.get();
        if (records != null && records.size() > 0) {
            List<String> messages = new ArrayList<>();
            for (LogRecord record : records) {
                String message = SerializeUtils.serialize(record);
                if (message == null) {
                    message = "serialize error...";
                }
                messages.add(message);
            }
            AgentLogger.getLogger().info("{}", SerializeUtils.serialize(messages));
        }
        CALL_LINKE_LOG_CONTEXT.remove();
    }

}
