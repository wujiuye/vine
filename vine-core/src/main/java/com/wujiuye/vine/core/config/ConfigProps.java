package com.wujiuye.vine.core.config;

/**
 * @author wujiuye 2020/09/07
 */
public class ConfigProps {

    /**
     * 日记是否打印方法描述符
     */
    private boolean logShowMethodDescriptor = true;

    public boolean isLogShowMethodDescriptor() {
        return logShowMethodDescriptor;
    }

    public void setLogShowMethodDescriptor(boolean logShowMethodDescriptor) {
        this.logShowMethodDescriptor = logShowMethodDescriptor;
    }

}
