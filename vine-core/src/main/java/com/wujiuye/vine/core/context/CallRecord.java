package com.wujiuye.vine.core.context;

/**
 * 调用记录
 *
 * @author wujiuye 2020/08/28
 */
public final class CallRecord {

    private String transactionId;
    private String className;
    private String methodName;
    private String descriptor;
    private Object[] params;
    private Throwable throwable;
    private Object returnValue;

    private long startTime;
    private long finishTime;
    private Long cntMs;

    private CallRecord next;
    private CallRecord pre;

    public CallRecord() {
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    void setNext(CallRecord next) {
        this.next = next;
    }

    void setPre(CallRecord pre) {
        this.pre = pre;
    }

    public CallRecord getNext() {
        return next;
    }

    public CallRecord getPre() {
        return pre;
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

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setCntMs(Long cntMs) {
        this.cntMs = cntMs;
    }

    public Long getCntMs() {
        cntMs = this.startTime > this.finishTime ? null : this.finishTime - this.startTime;
        return cntMs;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

}
