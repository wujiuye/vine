package com.wujiuye.vine.core.adapter.openfeign;

import com.wujiuye.vine.core.adapter.Adapter;
import com.wujiuye.vine.core.adapter.StackMethodVisitor;
import com.wujiuye.vine.core.util.ByteCodeUtils;
import org.objectweb.asm.*;

/**
 * OpenFeign 适配器
 *
 * @author wujiuye 2020/09/04
 */
public class SpringCloudOpenfeignAdapter implements Adapter {

    private final static String INTERCEPT_CLASS_NAME = "feign/ReflectiveFeign$BuildTemplateByResolvingArgs";
    private final static String INTERCEPT_METHOD_NAME = "create";
    private final static String INTERCEPT_METHOD_DEP = "([Ljava/lang/Object;)Lfeign/RequestTemplate;";

    private final static String INSTRUMENTATION_CLASS_NAME = "com/wujiuye/vine/spy/ContextSpy";
    private final static String INSTRUMENTATION_GET_METHOD_NAME = "getCurTransactionId";
    private final static String INSTRUMENTATION_GET_METHOD_DEP = "()Ljava/lang/String;";

    @Override
    public byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes) {
        if (!className.equalsIgnoreCase(INTERCEPT_CLASS_NAME)) {
            return null;
        }
        ClassReader classReader = new ClassReader(classBytes);
        String[] interfaces = classReader.getInterfaces();
        boolean falg = false;
        for (String cname : interfaces) {
            if ("feign/RequestTemplate$Factory".equalsIgnoreCase(cname)) {
                falg = true;
                break;
            }
        }
        if (!falg) {
            return null;
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
            @Override
            protected ClassLoader getClassLoader() {
                return loader;
            }
        };
        ClassVisitor classAdapter = new ClassVisitor(Opcodes.ASM6, classWriter) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (!name.equalsIgnoreCase(INTERCEPT_METHOD_NAME)
                        || !descriptor.equalsIgnoreCase(INTERCEPT_METHOD_DEP)) {
                    return methodVisitor;
                }
                return new StackMethodVisitor(Opcodes.ASM6, methodVisitor) {
                    private Label jumpLable = new Label();

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == Opcodes.ARETURN) {
                            // 操作数栈：
                            // TID_PARAM_NAME
                            // result
                            push(Adapter.TID_PARAM_NAME);

                            newArray(Type.getInternalName(String.class), 1);
                            dup();
                            visitInsn(Opcodes.ICONST_0);

                            callStatic(INSTRUMENTATION_CLASS_NAME,
                                    INSTRUMENTATION_GET_METHOD_NAME,
                                    INSTRUMENTATION_GET_METHOD_DEP);

                            dup();
                            visitInsn(Opcodes.ACONST_NULL);
                            // 操作数栈：(需要弹出的元素总数：5)
                            // TID
                            // 0
                            // newArray
                            // newArray
                            // TID_PARAM_NAME
                            // result
                            visitJumpInsn(Opcodes.IF_ACMPNE, jumpLable);

                            pop();
                            pop();
                            pop();
                            pop();
                            pop();
                            super.visitInsn(Opcodes.ARETURN);

                            visitLabel(jumpLable);
                            // 操作数栈：
                            // newArray
                            // TID_PARAM_NAME
                            // result
                            addElement();

                            // feign.RequestTemplate.header(java.lang.String, java.lang.String...)
                            String className = "Lfeign/RequestTemplate;";
                            String name = "header";
                            String dep = "(Ljava/lang/String;[Ljava/lang/String;)Lfeign/RequestTemplate;";
                            // 操作数栈：
                            // result
                            callVirtual(className, name, dep);
                        }
                        super.visitInsn(opcode);
                    }
                };
            }
        };
        try {
            classReader.accept(classAdapter, 0);
            byte[] result = classWriter.toByteArray();
            ByteCodeUtils.savaToFile(classReader.getClassName(), result);
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

}
