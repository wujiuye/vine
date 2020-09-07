package com.wujiuye.vine.core.config;

/**
 * 全局配置
 *
 * @author wujiuye 2020/08/28
 */
public final class GlobalConfigManager {

    private final static InstrumentationConfig INSTRUMENTATION_CONFIG = new InstrumentationConfig();
    private final static ConfigProps CONFIG_PROPS = new ConfigProps();

    public static InstrumentationConfig getInstrumentationConfig() {
        return INSTRUMENTATION_CONFIG;
    }

    public static ConfigProps getConfig() {
        return CONFIG_PROPS;
    }

}
