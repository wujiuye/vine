package com.wujiuye.vine.core.context;

/**
 * 上下文
 *
 * @author wujiuye 2020/08/28
 */
public final class Context {

    private final String transactionId;
    private CallRecord curRecord;
    /**
     * 调用链上是否出现异常
     */
    private boolean existError;

    public Context(String transactionId) {
        this.transactionId = transactionId;
        this.existError = false;
    }

    void setCurRecord(CallRecord curRecord) {
        this.curRecord = curRecord;
    }

    public CallRecord getCurRecord() {
        return curRecord;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setExistError(boolean existError) {
        this.existError = existError;
    }

    public boolean isExistError() {
        return existError;
    }

}
