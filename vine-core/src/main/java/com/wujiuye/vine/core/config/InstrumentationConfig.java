package com.wujiuye.vine.core.config;

import java.util.List;

/**
 * 插桩配置类
 *
 * @author wujiuye 2020/08/28
 */
public class InstrumentationConfig {

    private List<String> include_class;
    private List<String> include_package;
    private List<String> exclude_class;
    private List<String> exclude_package;

    public List<String> getInclude_class() {
        return include_class;
    }

    public void setInclude_class(List<String> include_class) {
        this.include_class = include_class;
    }

    public List<String> getInclude_package() {
        return include_package;
    }

    public void setInclude_package(List<String> include_package) {
        this.include_package = include_package;
    }

    public List<String> getExclude_class() {
        return exclude_class;
    }

    public void setExclude_class(List<String> exclude_class) {
        this.exclude_class = exclude_class;
    }

    public List<String> getExclude_package() {
        return exclude_package;
    }

    public void setExclude_package(List<String> exclude_package) {
        this.exclude_package = exclude_package;
    }

}
