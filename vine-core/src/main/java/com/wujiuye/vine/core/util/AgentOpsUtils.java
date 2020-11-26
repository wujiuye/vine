package com.wujiuye.vine.core.util;

import com.wujiuye.vine.core.config.ConfigConstant;
import com.wujiuye.vine.core.config.GlobalConfigManager;

import java.util.Collections;

/**
 * 参数解析工具
 *
 * @author wujiuye 2020/09/02
 */
public class AgentOpsUtils {

    /**
     * 解析和应用参数
     *
     * @param agentOps
     */
    public static void applySetting(String agentOps) {
        if (agentOps == null || agentOps.length() == 0) {
            throw new NullPointerException("未配置参数！");
        }
        // ','分隔参数
        String[] options = agentOps.split(",");
        for (String op : options) {
            if (op == null || op.trim().length() == 0) {
                continue;
            }
            String[] keyValue = op.split("=");
            if (keyValue.length != 2) {
                continue;
            }
            String key = keyValue[0];
            String value = keyValue[1];
            switch (key) {
                // 指定包名
                case ConfigConstant.PACKAGE:
                    if (!value.contains(".")) {
                        throw new RuntimeException("包名不正确！");
                    }
                    value = value.replace(".", "/") + "/*";
                    // 替换默认配置的
                    GlobalConfigManager.getInstrumentationConfig()
                            .setInclude_package(Collections.singletonList(value));
                    break;
                // 指定类输出路径
                case ConfigConstant.AGENT_CALSS_SAVA_PATH:
                    ByteCodeUtils.setSavaClassPath(value);
                    break;
                // 指定日记是否打印方法描述符
                case ConfigConstant.LOG_SHOW_METHOD_DESCRIPTOR:
                    GlobalConfigManager.getConfig().setLogShowMethodDescriptor(Boolean.parseBoolean(value));
                    break;
                // 设置采样率
                case ConfigConstant.SAMPLING_RATE:
                    GlobalConfigManager.getConfig().setSamplingRate(Integer.parseInt(value));
                default:
            }
        }
    }


}
