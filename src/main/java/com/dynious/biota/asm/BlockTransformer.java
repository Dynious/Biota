package com.dynious.biota.asm;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class BlockTransformer implements ITransformer
{
    private final String UPDATE_TICK = CoreTransformer.isObfurscated() ? "a" : "UPDATE_TICK";
    private final String UPDATE_TICK_DESC = CoreTransformer.isObfurscated() ? "(Lahb;IIILjava/util/Random;)V" :"(Lnet/minecraft/world/World;IIILjava/util/Random;)V";

    @Override
    public String[] getClasses()
    {
        return new String[] { "net.minecraft.block.Block" };
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(new BlockVisitor(classNode), 0);

        /*
        MethodNode methodNode = null;

        for (MethodNode aMethodNode : classNode.methods)
        {
            if (aMethodNode.name.equals(UPDATE_TICK) && aMethodNode.desc.equals(UPDATE_TICK_DESC))
            {
                methodNode = aMethodNode;
                break;
            }
        }

        if (methodNode != null)
        {
            classReader.accept(new BlockAdapter(methodNode), 0);
            System.out.println(Arrays.toString(methodNode.instructions.toArray()));
        }
        */

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private class BlockVisitor extends ClassVisitor
    {
        public BlockVisitor(ClassVisitor cv)
        {
            super(ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            if (name.equals(UPDATE_TICK) && desc.equals(UPDATE_TICK_DESC))
            {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                return new BlockAdapter(mv, access, name, desc);
            }
            else
            {
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

    private class BlockAdapter extends AdviceAdapter
    {
        public BlockAdapter(MethodVisitor methodVisitor, int access, String name, String desc)
        {
            super(ASM5, methodVisitor, access, name, desc);
        }

        @Override
        protected void onMethodEnter()
        {
            mv.visitIntInsn(AALOAD, 0);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "shouldStopUpdate", CoreTransformer.isObfurscated() ? "(Laji;)Z" : "(Lnet/minecraft/block/Block;)Z", false);
        }
    }
}
