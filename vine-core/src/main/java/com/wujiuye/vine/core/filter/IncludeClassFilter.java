package com.wujiuye.vine.core.filter;

import com.wujiuye.vine.core.config.InstrumentationConfig;
import com.wujiuye.vine.core.util.IncludeUtils;

/**
 * 类包含过滤器
 *
 * @author wujiuye 2020/08/28
 */
public class IncludeClassFilter extends InstrumentationConfigFliter {

    public IncludeClassFilter(InstrumentationConfig config) {
        super(config);
    }

    @Override
    public boolean filter(String className) {
        return !(IncludeUtils.includeComparator(config.getInclude_class(),
                (o1, o2) -> o1.equalsIgnoreCase(o2) ? 0 : -1,
                className)
                ||
                IncludeUtils.includeComparator(config.getInclude_package(),
                        (o1, o2) -> {
                            if (o1.contains("*")) {
                                return o2.startsWith(o1.replace("*", "")) ? 0 : -1;
                            } else {
                                return o2.equalsIgnoreCase(o1) ? 0 : -1;
                            }
                        }, className));
    }

}
