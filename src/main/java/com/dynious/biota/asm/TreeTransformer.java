package com.dynious.biota.asm;

import com.dynious.biota.Biota;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import static org.objectweb.asm.Opcodes.*;

public class TreeTransformer implements ITransformer
{
    private final static String SET_BLOCK = CoreTransformer.isObfurscated() ? "a" : "setBlockAndNotifyAdequately";
    private final static String SET_BLOCK_DESC = CoreTransformer.isObfurscated() ? "(Lahb;IIILaji;I)V" : "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V";

    @Override
    public String[] getClasses()
    {
        return new String[] { "net.minecraft.world.gen.feature.WorldGenAbstractTree" };
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        Biota.logger.debug("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        //Override removed method
        MethodVisitor mv = classNode.visitMethod(ACC_PROTECTED, SET_BLOCK, SET_BLOCK_DESC, null, null);

        //Call super
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, SET_BLOCK, SET_BLOCK_DESC, false);

        //Only if we update block to client (and we don' t generate the world for the fist time) call this event.
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, classNode.superName, CoreTransformer.isObfurscated() ? "a" : "doBlockNotify", "Z");
        Label label = new Label();
        mv.visitJumpInsn(IFEQ, label);

        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitVarInsn(ILOAD, 4);
        mv.visitVarInsn(ALOAD, 5);
        mv.visitVarInsn(ILOAD, 6);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantGrowth", CoreTransformer.isObfurscated() ? "(Lahb;IIILaji;I)V" : "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", false);

        mv.visitLabel(label);

        //Return
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);


        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
