package com.wujiuye.vine.core.adapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 封装操作
 *
 * @author wujiuye 2020/09/04
 */
public abstract class StackMethodVisitor extends MethodVisitor {

    public StackMethodVisitor(final int api, final MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    protected void dup() {
        visitInsn(Opcodes.DUP);
    }

    protected void pop() {
        visitInsn(Opcodes.POP);
    }

    protected void push(String str) {
        visitLdcInsn(str);
    }

    protected void aload(int index) {
        visitVarInsn(Opcodes.ALOAD, index);
    }

    protected void astore(int index) {
        visitVarInsn(Opcodes.ASTORE, index);
    }

    protected void callSpecial(String className, String methodName, String descriptor) {
        visitMethodInsn(Opcodes.INVOKESPECIAL, className, methodName, descriptor, false);
    }

    protected void callVirtual(String className, String methodName, String descriptor) {
        visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, methodName, descriptor, false);
    }

    protected void callInterface(String className, String methodName, String descriptor) {
        visitMethodInsn(Opcodes.INVOKEINTERFACE, className, methodName, descriptor, true);
    }

    protected void callStatic(String className, String methodName, String descriptor) {
        visitMethodInsn(Opcodes.INVOKESTATIC, className, methodName, descriptor, false);
    }

    protected void newArray(String className, int lenght) {
        switch (lenght) {
            case 0:
                visitInsn(Opcodes.ICONST_0);
                break;
            case 1:
                visitInsn(Opcodes.ICONST_1);
                break;
            case 2:
                visitInsn(Opcodes.ICONST_2);
                break;
            case 3:
                visitInsn(Opcodes.ICONST_3);
                break;
            case 4:
                visitInsn(Opcodes.ICONST_4);
                break;
            case 5:
                visitInsn(Opcodes.ICONST_5);
                break;
            default:
                visitVarInsn(Opcodes.BIPUSH, lenght);
        }
        visitTypeInsn(Opcodes.ANEWARRAY, className);
    }

    protected void addElement() {
        visitInsn(Opcodes.AASTORE);
    }

}
