package com.wujiuye.vine.core.instrumentation;

import com.wujiuye.vine.core.util.ByteCodeUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * 方法适配器
 *
 * @author wujiuye 2020/08/28
 */
public class MethodVisitorAdapter extends MethodVisitor {

    private String className;
    private boolean isStaticMethod = false;
    private String methodName;
    private String descriptor;
    private String[] paramDescriptors;
    private String returnDescriptor;

    private static class Ponicut {

        private String internalName;
        private String methodName;
        private String descriptor;
        private boolean interfaceFlag;

        public Ponicut(String internalName, String methodName, String descriptor, boolean interfaceFlag) {
            this.internalName = internalName;
            this.methodName = methodName;
            this.descriptor = descriptor;
            this.interfaceFlag = interfaceFlag;
        }

        public String getInternalName() {
            return internalName;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public String getMethodName() {
            return methodName;
        }

        public boolean isInterfaceFlag() {
            return interfaceFlag;
        }

    }

    private static final Ponicut BEFORE_PONICUT = new Ponicut("com/wujiuye/vine/spy/Spy",
            "before",
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/Object;)V",
            false);

    private static final Ponicut COMPLETE_PONICUT = new Ponicut("com/wujiuye/vine/spy/Spy",
            "complete",
            "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            false);

    public MethodVisitorAdapter(String className, int access, String methodName, String descriptor, MethodVisitor methodVisitor) {
        super(ASM6, methodVisitor);
        // 类名只是用于输出给埋点，替换为'.'
        this.className = className.replace("/", ".");
        this.methodName = methodName;
        this.descriptor = descriptor;
        // 根据方法的访问标志判断是否是静态方法
        if ((access & ACC_STATIC) == ACC_STATIC) {
            isStaticMethod = true;
        }
        // 根据方法描述符获取参数类型描述符
        this.paramDescriptors = ByteCodeUtils.getParamDescriptors(descriptor);
        // 获取返回值类型描述符
        this.returnDescriptor = ByteCodeUtils.getReturnDescriptor(descriptor);
    }

    private Label from = new Label(), to = new Label(), target = new Label();

    @Override
    public void visitCode() {
        super.visitCode();

        // 插入埋点代码，调用CallLogAspect的before方法
        this.visitLdcInsn(this.className);
        this.visitLdcInsn(this.methodName);
        this.visitLdcInsn(this.descriptor);
        if (paramDescriptors == null) {
            this.visitInsn(ACONST_NULL);
        } else {
            // 数组的大小
            if (paramDescriptors.length >= 4) {
                mv.visitVarInsn(BIPUSH, paramDescriptors.length);
            } else {
                switch (paramDescriptors.length) {
                    case 1:
                        mv.visitInsn(ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(ICONST_3);
                        break;
                    default:
                        mv.visitInsn(ICONST_0);
                }
            }
            // 创建Object数组
            mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(Object.class));

            // 方法第一个参数在局部变量表中的位置
            // 非静态方法排除this，即从1开始
            int localIndex = isStaticMethod ? 0 : 1;
            // 2. 给数组赋值
            for (int i = 0; i < paramDescriptors.length; i++) {
                // dup一份数组引用
                mv.visitInsn(DUP);
                // 访问数组的索引
                switch (i) {
                    case 0:
                        mv.visitInsn(ICONST_0);
                        break;
                    case 1:
                        mv.visitInsn(ICONST_1);
                        break;
                    case 2:
                        mv.visitInsn(ICONST_2);
                        break;
                    case 3:
                        mv.visitInsn(ICONST_3);
                        break;
                    default:
                        mv.visitVarInsn(BIPUSH, i);
                        break;
                }
                // 将基本数据类型转为Object类型
                // 调用基本数据类型对应的包装类型的valueOf静态方法
                String type = paramDescriptors[i];
                if ("Z".equals(type)) {
                    mv.visitVarInsn(ILOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Boolean.class),
                            "valueOf",
                            "(Z)Ljava/lang/Boolean;", false);
                } else if ("C".equals(type)) {
                    mv.visitVarInsn(ILOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Character.class),
                            "valueOf",
                            "(C)Ljava/lang/Character;", false);
                } else if ("B".equals(type)) {
                    mv.visitVarInsn(ILOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Byte.class),
                            "valueOf",
                            "(B)Ljava/lang/Byte;", false);
                } else if ("S".equals(type)) {
                    mv.visitVarInsn(ILOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Short.class),
                            "valueOf",
                            "(S)Ljava/lang/Short;", false);
                } else if ("I".equals(type)) {
                    mv.visitVarInsn(ILOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Integer.class),
                            "valueOf",
                            "(I)Ljava/lang/Integer;", false);
                } else if ("F".equals(type)) {
                    mv.visitVarInsn(FLOAD, localIndex++);
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Float.class),
                            "valueOf",
                            "(F)Ljava/lang/Float;", false);
                } else if ("J".equals(type)) {
                    // long类型占两个slot
                    mv.visitVarInsn(LLOAD, localIndex);
                    localIndex += 2;
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Long.class),
                            "valueOf",
                            "(J)Ljava/lang/Long;", false);
                } else if ("D".equals(type)) {
                    // double类型占两个slot
                    mv.visitVarInsn(DLOAD, localIndex);
                    localIndex += 2;
                    mv.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Double.class),
                            "valueOf",
                            "(D)Ljava/lang/Double;", false);
                } else {
                    // 数组或对象
                    mv.visitVarInsn(ALOAD, localIndex++);
                }
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                // 给数组指定下标元素赋值
                mv.visitInsn(AASTORE);
            }
        }
        this.visitMethodInsn(INVOKESTATIC, BEFORE_PONICUT.getInternalName(), BEFORE_PONICUT.getMethodName(),
                BEFORE_PONICUT.getDescriptor(), BEFORE_PONICUT.isInterfaceFlag());

        // 设置try代码块的开始
        this.visitLabel(from);
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case RETURN:
                // null入栈
                super.visitInsn(ACONST_NULL);
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            case IRETURN:
                super.visitInsn(DUP);
                if ("Z".equals(returnDescriptor)) {
                    this.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Boolean.class),
                            "valueOf",
                            "(Z)Ljava/lang/Boolean;", false);
                } else if ("B".equals(returnDescriptor)) {
                    this.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Byte.class),
                            "valueOf",
                            "(B)Ljava/lang/Byte;", false);
                } else if ("C".equals(returnDescriptor)) {
                    this.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Character.class),
                            "valueOf",
                            "(C)Ljava/lang/Character;", false);
                } else if ("S".equals(returnDescriptor)) {
                    this.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Short.class),
                            "valueOf",
                            "(S)Ljava/lang/Short;", false);
                } else {
                    this.visitMethodInsn(INVOKESTATIC,
                            Type.getInternalName(Integer.class),
                            "valueOf",
                            "(I)Ljava/lang/Integer;", false);
                }
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                // 调用埋点方法
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            case FRETURN:
                super.visitInsn(DUP);
                this.visitMethodInsn(INVOKESTATIC,
                        Type.getInternalName(Float.class),
                        "valueOf",
                        "(F)Ljava/lang/Float;", false);
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            case LRETURN:
                // long占两个slot
                super.visitInsn(DUP2);
                this.visitMethodInsn(INVOKESTATIC,
                        Type.getInternalName(Long.class),
                        "valueOf",
                        "(J)Ljava/lang/Long;", false);
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            case DRETURN:
                // double占两个slot
                super.visitInsn(DUP2);
                this.visitMethodInsn(INVOKESTATIC,
                        Type.getInternalName(Double.class),
                        "valueOf",
                        "(D)Ljava/lang/Double;", false);
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            case ARETURN:
                super.visitInsn(DUP);
                this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
                this.visitLdcInsn(this.className);
                this.visitLdcInsn(this.methodName);
                this.visitLdcInsn(this.descriptor);
                this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                        COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());
                break;
            default:
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        this.visitLabel(to);
        this.visitLabel(target);

        this.visitInsn(DUP);
        this.visitTypeInsn(CHECKCAST, Type.getInternalName(Object.class));
        this.visitLdcInsn(this.className);
        this.visitLdcInsn(this.methodName);
        this.visitLdcInsn(this.descriptor);
        this.visitMethodInsn(INVOKESTATIC, COMPLETE_PONICUT.getInternalName(), COMPLETE_PONICUT.getMethodName(),
                COMPLETE_PONICUT.getDescriptor(), COMPLETE_PONICUT.isInterfaceFlag());

        // 抛出Throwable异常
        this.visitInsn(ATHROW);

        this.visitTryCatchBlock(from, to, target, Type.getInternalName(Throwable.class));
        super.visitMaxs(maxStack, maxLocals);
    }

}
