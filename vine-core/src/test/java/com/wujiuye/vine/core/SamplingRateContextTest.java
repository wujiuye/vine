package com.wujiuye.vine.core;

import com.wujiuye.vine.core.context.SamplingRateContext;
import org.junit.Test;

public class SamplingRateContextTest {

    @Test
    public void testSamplingRate() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("是否需要采样：" + SamplingRateContext.needSampling());
            SamplingRateContext.inc();
            SamplingRateContext.reset();
        }
    }

}
