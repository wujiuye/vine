package com.wujiuye.vine.core.aspect;

import com.wujiuye.vine.core.context.CallLinkContext;
import com.wujiuye.vine.core.context.CallRecord;
import com.wujiuye.vine.core.context.ContextListener;
import com.wujiuye.vine.core.util.AgentLogger;
import com.wujiuye.vine.core.util.ExceptionStackHelper;
import com.wujiuye.vine.core.util.ReflectionUtils;
import com.wujiuye.vine.core.util.SerializeUtils;

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
        LogRecord logRecord = toLog(callRecord, printParamAndReturnValue);
        CALL_LINKE_LOG_CONTEXT.get().add(logRecord);
    }

    private LogRecord toLog(CallRecord record, boolean printParamAndReturnValue) {
        LogRecord logRecord = new LogRecord();
        logRecord.setTransactionId(record.getTransactionId());
        logRecord.setClassName(record.getClassName());
        logRecord.setMethodName(record.getMethodName());
        logRecord.setDescriptor(record.getDescriptor());
        logRecord.setStartTime(record.getStartTime());
        logRecord.setFinishTime(record.getFinishTime());
        logRecord.setCntMs(record.getCntMs());
        if (record.getThrowable() != null) {
            logRecord.setError(true);
            logRecord.setErrorMsg(ExceptionStackHelper.getExceptionStackInfo(record.getThrowable(), 15));
        }
        if (printParamAndReturnValue) {
            logRecord.setParams(objToString(record.getParams()));
            logRecord.setReturnValue(objToString(record.getReturnValue()));
        }
        return logRecord;
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

    public static final class LogRecord {
        private String transactionId;
        private String className;
        private String methodName;
        private String descriptor;
        private String params;
        private boolean error = false;
        private String errorMsg;
        private String returnValue;
        private long startTime;
        private long finishTime;
        private long cntMs;

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        public String getParams() {
            return params;
        }

        public void setParams(String params) {
            this.params = params;
        }

        public boolean isError() {
            return error;
        }

        public void setError(boolean error) {
            this.error = error;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getReturnValue() {
            return returnValue;
        }

        public void setReturnValue(String returnValue) {
            this.returnValue = returnValue;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(long finishTime) {
            this.finishTime = finishTime;
        }

        public Long getCntMs() {
            return cntMs;
        }

        public void setCntMs(Long cntMs) {
            this.cntMs = cntMs;
        }

    }

    private final static Set<String> BEAN_CLASS_NAME;

    static {
        BEAN_CLASS_NAME = new HashSet<>();
        BEAN_CLASS_NAME.add("do");
        BEAN_CLASS_NAME.add("dto");
        BEAN_CLASS_NAME.add("po");
        BEAN_CLASS_NAME.add("ao");
        BEAN_CLASS_NAME.add("from");
        BEAN_CLASS_NAME.add("entity");
        BEAN_CLASS_NAME.add("config");
        BEAN_CLASS_NAME.add("props");
    }

    private static String objToString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (ReflectionUtils.isPrimitive(obj)) {
            return obj.toString();
        }
        StringBuilder builder = new StringBuilder();
        if (obj.getClass().isArray()) {
            Object[] array = (Object[]) obj;
            Object[] newArray = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                if (!supporSerialize(array[i])) {
                    newArray[i] = "serializable error";
                    continue;
                }
                newArray[i] = array[i];
            }
            String result = SerializeUtils.serialize(newArray);
            if (result != null) {
                builder.append(result);
            }
        } else {
            if (!supporSerialize(obj)) {
                builder.append("serializable error");
            } else {
                String result = SerializeUtils.serialize(obj);
                if (result != null) {
                    builder.append(result);
                }
            }
        }
        return builder.toString();
    }

    private static boolean supporSerialize(Object object) {
        if (object == null) {
            return true;
        }
        if (ReflectionUtils.isPrimitive(object)) {
            return true;
        }
        if (object.getClass().isAnnotation()) {
            return false;
        }
        if (object.getClass().isArray()) {
            Object[] array = (Object[]) object;
            if (array[0] != null) {
                Object item = array[0];
                return supporSerialize(item);
            }
        }
        if (object instanceof Collection) {
            for (Object item : (Collection<?>) object) {
                return supporSerialize(item);
            }
        }
        String className = object.getClass().getName().toLowerCase();
        if (className.startsWith("java.") || className.startsWith("sun.")) {
            return false;
        }
        if (className.startsWith("org.springframework") || className.startsWith("com.fasterxml")) {
            return false;
        }
        for (String name : BEAN_CLASS_NAME) {
            if (className.endsWith(name)) {
                return true;
            }
        }
        return false;
    }

}
