package com.wujiuye.vine.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.jar.JarFile;

/**
 * Agent应用
 * 启动示例： java -javaagent:{project_url}/vine-agent/target/vine-agent.jar={project_url}/vine-core/target/vine-core-jar-with-dependencies.jar,{project_url}/vine-spy/target/vine-spy.jar=agent.package={package} -jar {jar}
 *
 * @author wujiuye 2020/08/28
 */
public class VineAgentApplication {

    public static void premain(String agentOps, Instrumentation instrumentation) {
        try {
            main(agentOps, instrumentation, false);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void agentmain(String agentOps, Instrumentation instrumentation) {
        try {
            main(agentOps, instrumentation, true);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 类加载器：sun.misc.Launcher$AppClassLoader
     *
     * @param agentOps
     * @param instrumentation
     */
    private static void main(String agentOps, Instrumentation instrumentation, boolean agent) throws Throwable {
        // agent-core.jar
        String[] jarPath = agentOps.substring(0, agentOps.indexOf("=")).split(",");
        String agentJar = jarPath[0];
        File agentJarFile = new File(agentJar);
        if (!agentJarFile.exists()) {
            System.out.println("Agent jar file does not exist: " + agentJarFile);
            return;
        }

        // agent-spy.jar
        String agentSpyJar = jarPath[1];
        File spyJarFile = new File(agentSpyJar);
        if (!spyJarFile.exists()) {
            System.out.println("Spy jar file does not exist: " + spyJarFile);
            return;
        }
        // load agent-spy.jar
        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));

        // params
        String opsParams = agentOps.substring(agentOps.indexOf("=") + 1);
        System.out.println("ops params: " + opsParams);

        // load agent-core.jar
        final ClassLoader agentLoader = loadOrDefineClassLoader(agentJarFile);
        Class<?> transFormer = agentLoader.loadClass("com.wujiuye.vine.core.VineClassFileTransformer");
        Constructor<?> constructor = transFormer.getConstructor(String.class);
        Object instance = constructor.newInstance(opsParams);

        if (!agent) {
            instrumentation.addTransformer((ClassFileTransformer) instance);
        } else {
            instrumentation.addTransformer((ClassFileTransformer) instance, true);
            Class<?>[] classs = instrumentation.getAllLoadedClasses();
            for (Class<?> cla : classs) {
                try {
                    // 重转换类，重转换类不允许给类添加或移除字段
                    instrumentation.retransformClasses(cla);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
            // 完成后可将转换器移除
            instrumentation.removeTransformer((ClassFileTransformer) instance);
        }
    }

    private static volatile ClassLoader onionClassLoader;

    private static ClassLoader loadOrDefineClassLoader(File agentJar) throws Throwable {
        if (onionClassLoader == null) {
            onionClassLoader = new VineClassLoader(new URL[]{agentJar.toURI().toURL()});
        }
        return onionClassLoader;
    }

}
