package com.wujiuye.vine.core.instrumentation;

import com.wujiuye.vine.core.util.ByteCodeUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.objectweb.asm.Opcodes.*;

/**
 * 类字节码插桩工厂
 *
 * @author wujiuye 2020/08/28
 */
public class ClassInstrumentationFactory {

    private final static Object ENTRY = new Object();

    /**
     * 记录已经插过桩的类
     */
    private final static ConcurrentMap<String, Object> MODIFY_CLASS_MAP = new ConcurrentHashMap<>();

    public static byte[] modifyClass(byte[] classfileBuffer) {
        ClassReader classReader = new ClassReader(classfileBuffer);
        // 过滤接口
        if ((classReader.getAccess() & ACC_INTERFACE) == ACC_INTERFACE) {
            return classfileBuffer;
        }
        // 过滤注解
        if ((classReader.getAccess() & ACC_ANNOTATION) == ACC_ANNOTATION) {
            return classfileBuffer;
        }
        // 过滤枚举类
        if ((classReader.getAccess() & ACC_ENUM) == ACC_ENUM) {
            return classfileBuffer;
        }
        String supperName = classReader.getSuperName();
        // 过滤异常类
        if ("java/lang/RuntimeException".equals(supperName)
                || "java/lang/Exception".equals(supperName)
                || "java/lang/Throwable".equals(supperName)) {
            return null;
        }
        if (MODIFY_CLASS_MAP.containsKey(classReader.getClassName())) {
            return classfileBuffer;
        }
        try {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            ClassVisitorAdapter classAdapter = new ClassVisitorAdapter(classReader.getClassName(), classWriter);
            classReader.accept(classAdapter, 0);
            byte[] bytes = classWriter.toByteArray();
            ByteCodeUtils.savaToFile(classReader.getClassName(), bytes);
            MODIFY_CLASS_MAP.put(classReader.getClassName(), ENTRY);
            return bytes;
        } catch (Throwable throwable) {
            MODIFY_CLASS_MAP.remove(classReader.getClassName());
            throw throwable;
        }
    }

}
