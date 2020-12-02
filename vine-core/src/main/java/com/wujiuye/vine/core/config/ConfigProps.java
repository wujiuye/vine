package com.wujiuye.vine.core.config;

/**
 * @author wujiuye 2020/09/07
 */
public class ConfigProps {

    /**
     * 日记是否打印方法描述符
     */
    private boolean logShowMethodDescriptor = true;
    /**
     * 采样率，取值范围[1,100]
     * 默认为10，即：10%
     */
    private int samplingRate = 10;

    public boolean isLogShowMethodDescriptor() {
        return logShowMethodDescriptor;
    }

    public void setLogShowMethodDescriptor(boolean logShowMethodDescriptor) {
        this.logShowMethodDescriptor = logShowMethodDescriptor;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

}
