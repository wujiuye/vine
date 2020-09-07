package com.wujiuye.vine.core;

import com.wujiuye.vine.core.adapter.Adapter;
import com.wujiuye.vine.core.adapter.openfeign.SpringCloudOpenfeignAdapter;
import com.wujiuye.vine.core.adapter.webmvc.SpringWebmvcAdapter;
import com.wujiuye.vine.core.aspect.MethodCallListener;
import com.wujiuye.vine.core.config.GlobalConfigManager;
import com.wujiuye.vine.core.config.InstrumentationConfig;
import com.wujiuye.vine.core.filter.ClassFilterChain;
import com.wujiuye.vine.core.filter.ExincludeClassFilter;
import com.wujiuye.vine.core.filter.IncludeClassFilter;
import com.wujiuye.vine.core.instrumentation.ClassInstrumentationFactory;
import com.wujiuye.vine.core.util.AgentOpsUtils;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

/**
 * class重写，字节码插桩
 *
 * @author wujiuye 2020/08/28
 */
public class VineClassFileTransformer implements ClassFileTransformer {

    private ClassFilterChain classFilterChain;
    private List<Adapter> adapters;

    public VineClassFileTransformer(String agentOps) {
        AgentOpsUtils.applySetting(agentOps);
        this.initAspect();
        this.initFilterChain();
        this.adapters = Arrays.asList(
                new SpringWebmvcAdapter(),
                new SpringCloudOpenfeignAdapter()
        );
    }

    private void initAspect() {
        try {
            MethodCallListener.init();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void initFilterChain() {
        InstrumentationConfig config = GlobalConfigManager.getInstrumentationConfig();
        classFilterChain = new ClassFilterChain();
        classFilterChain.addLast(new IncludeClassFilter(config))
                .addLast(new ExincludeClassFilter(config));
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        // 一定要排除
        if (className.startsWith("sun/") || className.startsWith("java/")) {
            return classfileBuffer;
        }
        // 把自己排除掉
        if (className.startsWith("com/wujiuye/vine/")) {
            return classfileBuffer;
        }
        // 排除调动态代理类
        if (className.contains("CGLIB")) {
            return classfileBuffer;
        }
        // 第三方框架适配
        for (Adapter adapter : adapters) {
            byte[] result = adapter.modifyClass(loader, className, classfileBuffer);
            if (result != null) {
                return result;
            }
        }
        // 过滤器过滤
        if (classFilterChain.filter(className)) {
            return classfileBuffer;
        }
        return ClassInstrumentationFactory.modifyClass(classfileBuffer);
    }

}
