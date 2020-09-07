package com.wujiuye.vine.core.adapter.webmvc;

import com.wujiuye.vine.core.adapter.Adapter;
import com.wujiuye.vine.core.adapter.StackMethodVisitor;
import com.wujiuye.vine.core.util.ByteCodeUtils;
import org.objectweb.asm.*;

/**
 * Spring Webmvc 适配器
 *
 * @author wujiuye 2020/09/04
 */
public class SpringWebmvcAdapter implements Adapter {

    private final static String INTERCEPT_CLASS_NAME = "org/springframework/web/servlet/DispatcherServlet";
    private final static String INTERCEPT_METHOD_NAME = "doDispatch";
    private final static String INTERCEPT_METHOD_DEP = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)V";

    private final static String INSTRUMENTATION_CLASS_NAME = "com/wujiuye/vine/spy/ContextSpy";
    private final static String INSTRUMENTATION_SET_METHOD_NAME = "applyCurTransactionId";
    private final static String INSTRUMENTATION_SET_METHOD_DEP = "(Ljava/lang/String;)V";

    @Override
    public byte[] modifyClass(ClassLoader loader, String className, byte[] classBytes) {
        if (!className.equalsIgnoreCase(INTERCEPT_CLASS_NAME)) {
            return null;
        }
        try {
            ClassReader classReader = new ClassReader(classBytes);
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
                    if (!name.equalsIgnoreCase(INTERCEPT_METHOD_NAME) || !descriptor.equalsIgnoreCase(INTERCEPT_METHOD_DEP)) {
                        return methodVisitor;
                    } else {
                        return new StackMethodVisitor(Opcodes.ASM6, methodVisitor) {

                            private Label from = new Label();
                            private Label to = new Label();
                            private Label target = new Label();

                            @Override
                            public void visitCode() {
                                super.visitCode();
                                aload(1);
                                push(Adapter.TID_PARAM_NAME);
                                callInterface("javax/servlet/http/HttpServletRequest",
                                        "getHeader",
                                        "(Ljava/lang/String;)Ljava/lang/String;");
                                callStatic(INSTRUMENTATION_CLASS_NAME,
                                        INSTRUMENTATION_SET_METHOD_NAME,
                                        INSTRUMENTATION_SET_METHOD_DEP);
                                visitLabel(from);
                            }

                            @Override
                            public void visitInsn(int opcode) {
                                switch (opcode) {
                                    case Opcodes.RETURN:
                                    case Opcodes.IRETURN:
                                    case Opcodes.LRETURN:
                                    case Opcodes.FRETURN:
                                    case Opcodes.DRETURN:
                                    case Opcodes.ARETURN:
                                        callStatic(INSTRUMENTATION_CLASS_NAME,
                                                "clearSetting",
                                                "()V");
                                        break;
                                    default:
                                }
                                super.visitInsn(opcode);
                            }

                            @Override
                            public void visitMaxs(int maxStack, int maxLocals) {
                                visitLabel(to);
                                visitLabel(target);

                                callStatic(INSTRUMENTATION_CLASS_NAME,
                                        "clearSetting",
                                        "()V");

                                visitInsn(Opcodes.ATHROW);
                                super.visitMaxs(maxStack, maxLocals);
                            }
                        };
                    }
                }
            };
            classReader.accept(classAdapter, 0);
            byte[] bytes = classWriter.toByteArray();
            ByteCodeUtils.savaToFile(classReader.getClassName(), bytes);
            return bytes;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

}
