package com.wujiuye.vine.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字节码工具类
 *
 * @author wujiuye 2020/08/28
 */
public class ByteCodeUtils {

    private final static Pattern PATTERN = Pattern.compile("(L.*?;|\\[{0,2}L.*?;|[ZCBSIFJD]|\\[{0,2}[ZCBSIFJD]{1})");

    private static String SAVA_CLASS_PATH;

    public synchronized static void setSavaClassPath(String savaClassPath) {
        if (!savaClassPath.endsWith("/")) {
            SAVA_CLASS_PATH = savaClassPath + "/";
            return;
        }
        SAVA_CLASS_PATH = savaClassPath;
    }

    private static String getSavaClassPath() {
        return SAVA_CLASS_PATH;
    }

    /**
     * 将字节码输出为class文件
     *
     * @throws IOException
     */
    public static void savaToFile(String className, byte[] byteCode) {
        String classSavaPath = getSavaClassPath();
        if (classSavaPath == null) {
            return;
        }
        File file = new File(classSavaPath + className.replace("/", ".") + ".class");
        try {
            if ((!file.exists() || file.delete()) && file.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(byteCode);
                }
            }
        } catch (Throwable throwable) {
            AgentLogger.getLogger().error("class name sava error: "
                    + className + " class sava path:" + classSavaPath, throwable);
        }
    }

    /**
     * 根据方法描述符获取方法参数
     *
     * @param methodDescriptor 方法描述符
     * @return
     */
    public static String[] getParamDescriptors(String methodDescriptor) {
        List<String> paramDescriptors = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(methodDescriptor.substring(0, methodDescriptor.lastIndexOf(')') + 1));
        while (matcher.find()) {
            paramDescriptors.add(matcher.group(1));
        }
        if (paramDescriptors.size() == 0) {
            return null;
        }
        return paramDescriptors.toArray(new String[0]);
    }

    public static String getReturnDescriptor(String methodDescriptor) {
        return methodDescriptor.substring(methodDescriptor.lastIndexOf(")") + 1);
    }

}
