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
    volatile private static int curSamplingRate = 10;

    /**
     * 是否需要采样
     *
     * @return
     */
    public static boolean needSampling() {
        return SAMPLING_RATE.intValue() < curSamplingRate;
    }

    public static void inc() {
        SAMPLING_RATE.increment();
    }

    public static void reset() {
        if (SAMPLING_RATE.intValue() >= 100) {
            curSamplingRate = GlobalConfigManager.getConfig().getSamplingRate();
            SAMPLING_RATE.reset();
        }
    }

}
