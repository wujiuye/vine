package com.wujiuye.vine.core.filter;

import com.wujiuye.vine.core.config.InstrumentationConfig;

/**
 * @author wujiuye 2020/08/28
 */
public abstract class InstrumentationConfigFliter implements ClassFilter {

    protected InstrumentationConfig config;

    public InstrumentationConfigFliter(InstrumentationConfig config) {
        this.config = config;
    }

}
