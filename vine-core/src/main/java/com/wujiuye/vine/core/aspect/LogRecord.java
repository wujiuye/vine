package com.wujiuye.vine.core.aspect;

import com.wujiuye.vine.core.context.CallRecord;
import com.wujiuye.vine.core.util.ExceptionStackHelper;
import com.wujiuye.vine.core.util.LogBeanSerializeUtils;

/**
 * Log POJO
 *
 * @author wujiuye 2020/10/28
 */
public class LogRecord {

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

    /**
     * 调用记录转为可输出的日记记录
     *
     * @param record                   调用记录
     * @param printParamAndReturnValue 是否打印参数和返回值
     * @return
     */
    public static LogRecord toLogRecord(CallRecord record, boolean printParamAndReturnValue) {
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
            logRecord.setParams(LogBeanSerializeUtils.objToString(record.getParams()));
            logRecord.setReturnValue(LogBeanSerializeUtils.objToString(record.getReturnValue()));
        }
        return logRecord;
    }

}
