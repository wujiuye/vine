package com.wujiuye.vine.core.instrumentation;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;

/**
 * 类适配器
 *
 * @author wujiuye 2020/08/28
 */
public class ClassVisitorAdapter extends ClassVisitor {

    private final static Set<String> FILTER_METHODS = new HashSet<>();

    static {
        // 不对类实例初始化方法注入
        FILTER_METHODS.add("<init>");
        // 不对类初始化方法注入
        FILTER_METHODS.add("<clinit>");
        // 过滤main方法
        FILTER_METHODS.add("main");
        // 不注入类重写的Object父类的方法
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method method : methods) {
            FILTER_METHODS.add(method.getName());
        }
    }

    private String className;
    private Set<String> filterGetSetFieldMethods = new HashSet<>();

    public ClassVisitorAdapter(String className, ClassWriter classWriter) {
        super(ASM6, classWriter);
        this.className = className;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        // 过滤字段的get、set方法，非精确过滤
        String mname = ("" + name.charAt(0)).toUpperCase() + name.substring(1);
        filterGetSetFieldMethods.add("get" + mname);
        filterGetSetFieldMethods.add("set" + mname);
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        // 不对抽象、native等方法注入
        if ((access & ACC_ABSTRACT) != 0
                || (access & ACC_NATIVE) != 0
                || (access & ACC_BRIDGE) != 0
                || (access & ACC_SYNTHETIC) != 0
                || (access & ACC_VARARGS) != 0) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        if (FILTER_METHODS.contains(name) || filterGetSetFieldMethods.contains(name)) {
            return super.visitMethod(access, name, descriptor, signature, exceptions);
        }
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitorAdapter(className, access, name, descriptor, mv);
    }

}
