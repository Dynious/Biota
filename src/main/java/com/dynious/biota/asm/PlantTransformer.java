package com.dynious.biota.asm;

import com.dynious.biota.api.IPlant;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class PlantTransformer implements ITransformer
{
    private final static String ADDED = CoreTransformer.isObfurscated() ? "b" : "onBlockAdded";
    private final static String ADDED_DESC = CoreTransformer.isObfurscated() ? "(Lahb;III)V" : "(Lnet/minecraft/world/World;III)V";

    private final static String REMOVED = CoreTransformer.isObfurscated() ? "a" : "breakBlock";
    private final static String REMOVED_DESC = CoreTransformer.isObfurscated() ? "(Lahb;IIILaji;I)V" : "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V";

    private final static String COLOR = CoreTransformer.isObfurscated() ? "d" : "colorMultiplier";
    private final static String COLOR_DESC = CoreTransformer.isObfurscated() ? "(Lahl;III)I" : "(Lnet/minecraft/world/IBlockAccess;III)I";

    private final static String TICK = CoreTransformer.isObfurscated() ? "a" : "updateTick";
    private final static String TICK_DESC = CoreTransformer.isObfurscated() ? "(Lahb;IIILjava/util/Random;)V" : "(Lnet/minecraft/world/World;IIILjava/util/Random;)V";

    @Override
    public String[] getClasses()
    {
        return PlantConfigLoader.INSTANCE.getPlantClassNames();
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        System.out.println("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        //Add IPlant interface for easy plant checking
        classNode.interfaces.add(Type.getInternalName(IPlant.class));

        boolean shouldChangeColor = PlantConfigLoader.INSTANCE.shouldPlantChangeColor(transformedName);

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
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockAdded", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false));

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
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockRemoved", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false));

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
                        list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "getColor", "(III)I", false));

                        methodNode.instructions.insertBefore(node, list);
                    }
                }
            }
            else if (methodNode.name.equals(TICK) && methodNode.desc.equals(TICK_DESC))
            {
                foundTick = true;

                InsnList list = new InsnList();
                LabelNode labelNode = new LabelNode();

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantTick", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)Z" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)Z", false));
                list.add(new JumpInsnNode(IFNE, labelNode));

                methodNode.instructions.insert(list);

                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while(iterator.hasNext())
                {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == RETURN)
                    {
                        methodNode.instructions.insertBefore(node, labelNode);
                        break;
                    }
                }

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
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockAdded", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false);

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
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockRemoved", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false);

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

            Label label = new Label();

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantTick", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)Z" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)Z", false);
            mv.visitJumpInsn(IFNE, label);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ILOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ILOAD, 4);
            mv.visitVarInsn(ALOAD, 5);
            mv.visitMethodInsn(INVOKESPECIAL, classNode.superName, TICK, TICK_DESC, false);

            mv.visitLabel(label);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);

        }

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
