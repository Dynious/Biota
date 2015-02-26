package com.dynious.biota.asm.transformers;

import com.dynious.biota.api.IPlant;
import com.dynious.biota.asm.Hooks;
import com.dynious.biota.asm.ITransformer;
import com.dynious.biota.asm.MethodFieldObfHelper;
import com.dynious.biota.asm.PlantTransformerConfig;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import squeek.asmhelper.applecore.ObfHelper;
import squeek.asmhelper.applecore.ObfRemappingClassWriter;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class PlantTransformer implements ITransformer
{
    private final static String ADDED = ObfHelper.isObfuscated() ? MethodFieldObfHelper.method("net.minecraft.block.Block", "func_149726_b") : "onBlockAdded";
    private final static String ADDED_DESC = ObfHelper.desc("(Lnet/minecraft/world/World;III)V");

    private final static String REMOVED = ObfHelper.isObfuscated() ? MethodFieldObfHelper.method("net.minecraft.block.Block", "func_149749_a") : "breakBlock";
    private final static String REMOVED_DESC = ObfHelper.desc("(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V");

    private final static String COLOR = ObfHelper.isObfuscated() ? MethodFieldObfHelper.method("net.minecraft.block.Block", "func_149720_d") : "colorMultiplier";
    private final static String COLOR_DESC = ObfHelper.desc("(Lnet/minecraft/world/IBlockAccess;III)I");

    private final static String TICK = ObfHelper.isObfuscated() ? MethodFieldObfHelper.method("net.minecraft.block.Block", "func_149674_a") : "updateTick";
    private final static String TICK_DESC = ObfHelper.desc("(Lnet/minecraft/world/World;IIILjava/util/Random;)V");

    @Override
    public String[] getClasses()
    {
        return PlantTransformerConfig.INSTANCE.getPlantClassNames();
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        //Biota.logger.debug("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        //Add IPlant interface for easy plant checking
        classNode.interfaces.add(Type.getInternalName(IPlant.class));

        boolean shouldChangeColor = PlantTransformerConfig.INSTANCE.shouldPlantChangeColor(transformedName);

        boolean foundAdded = false;
        boolean foundRemoved = false;
        boolean foundColor = false;
        boolean foundTick = false;

        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals(ADDED) && methodNode.desc.equals(ADDED_DESC))
            {
                foundAdded = true;

                InsnList list = new InsnList();

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockAdded", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false));

                methodNode.instructions.insert(list);
            }
            else if (methodNode.name.equals(REMOVED) && methodNode.desc.equals(REMOVED_DESC))
            {
                foundRemoved = true;

                InsnList list = new InsnList();

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockRemoved", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false));

                methodNode.instructions.insert(list);
            }
            else if (shouldChangeColor && methodNode.name.equals(COLOR) && methodNode.desc.equals(COLOR_DESC))
            {
                foundColor = true;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while(iterator.hasNext())
                {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == IRETURN)
                    {
                        InsnList list = new InsnList();

                        list.add(new VarInsnNode(ILOAD, 2));
                        list.add(new VarInsnNode(ILOAD, 4));
                        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "getColor", ObfHelper.desc("(III)I"), false));

                        methodNode.instructions.insertBefore(node, list);
                    }
                }
            }
            else if (methodNode.name.equals(TICK) && methodNode.desc.equals(TICK_DESC))
            {
                foundTick = true;
                InsnList list = new InsnList();

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantTick", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false));

                methodNode.instructions.insert(list);
            }
        }

        if (!foundAdded)
        {
            //Override added method
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, ADDED, ADDED_DESC, null, null);

            //Call our method
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockAdded", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false);

            //Call super
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, ADDED, ADDED_DESC, false);

            //Return
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
        }
        if (!foundRemoved)
        {
            //Override removed method
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, REMOVED, REMOVED_DESC, null, null);

            //Call our method
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockRemoved", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false);

            //Call super
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitVarInsn(ILOAD, 6);
            mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, REMOVED, REMOVED_DESC, false);

            //Return
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
        }
        if (shouldChangeColor && !foundColor)
        {
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, COLOR, COLOR_DESC, null, null);

            //Call super and get color int
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, COLOR, COLOR_DESC, false);

            //Call our color change method
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "getColor",  "(III)I", false);

            //Return altered color
            mv.visitInsn(IRETURN);
            mv.visitMaxs(0, 0);
        }
        if (!foundTick)
        {
            MethodVisitor mv = classNode.visitMethod(ACC_PUBLIC, TICK, TICK_DESC, null, null);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantTick", ObfHelper.desc("(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V"), false);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, TICK, TICK_DESC, false);

            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);

        }

        ClassWriter classWriter = new ObfRemappingClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
