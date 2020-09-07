package com.wujiuye.vine.core.config;

/**
 * 配置项常量
 *
 * @author wujiuye 2020/08/29
 */
public interface ConfigConstant {

    /**
     * 指定对哪个包下的类进行插桩
     */
    String PACKAGE = "agent.package";

    /**
     * 配置插桩后的字节码输出，如果不配置则不输出
     */
    String AGENT_CALSS_SAVA_PATH = "agent.classSavaPath";

    /**
     * 日记是否打印方法描述符
     */
    String LOG_SHOW_METHOD_DESCRIPTOR = "log.showMethodDescriptor";

}
