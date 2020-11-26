package com.wujiuye.vine.core.context;

import com.wujiuye.vine.core.config.GlobalConfigManager;

import java.util.concurrent.atomic.LongAdder;

/**
 * 采样控制
 *
 * @author wujiuye 2020/11/26
 */
public class SamplingRateContext {

    private final static LongAdder SAMPLING_RATE = new LongAdder();

    /**
     * 是否需要采样
     *
     * @return
     */
    public static boolean needSampling() {
        return SAMPLING_RATE.intValue() == 0;
    }

    public static void inc() {
        SAMPLING_RATE.increment();
    }

    public static void reset() {
        if (GlobalConfigManager.getConfig().getSamplingRate() >= SAMPLING_RATE.intValue()) {
            SAMPLING_RATE.reset();
        }
    }

}
