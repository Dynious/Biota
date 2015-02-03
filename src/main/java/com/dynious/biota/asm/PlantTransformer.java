package com.dynious.biota.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Arrays;

import static org.objectweb.asm.Opcodes.*;

public class PlantTransformer implements ITransformer
{
    private final static String ADDED = CoreTransformer.isObfurscated() ? "b" : "onBlockAdded";
    private final static String ADDED_DESC = CoreTransformer.isObfurscated() ? "(Lahb;III)V" : "(Lnet/minecraft/world/World;III)V";

    private final static String REMOVED = CoreTransformer.isObfurscated() ? "a" : "breakBlock";
    private final static String REMOVED_DESC = CoreTransformer.isObfurscated() ? "(Lahb;IIILaji;I)V" : "(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V";

    @Override
    public String[] getClasses()
    {
        return new String[]
                {
                        "net.minecraft.block.BlockGrass",
                        "net.minecraft.block.BlockSapling",
                        "net.minecraft.block.BlockOldLog",
                        "net.minecraft.block.BlockOldLeaf",
                        "net.minecraft.block.BlockTallGrass",
                        "net.minecraft.block.BlockFlower",
                        "net.minecraft.block.BlockMushroom",
                        "net.minecraft.block.BlockCrops",
                        "net.minecraft.block.BlockCactus",
                        "net.minecraft.block.BlockPumpkin",
                        "net.minecraft.block.BlockHugeMushroom",
                        "net.minecraft.block.BlockMelon",
                        "net.minecraft.block.BlockStem",
                        "net.minecraft.block.BlockVine",
                        "net.minecraft.block.BlockMycelium",
                        "net.minecraft.block.BlockLilyPad",
                        "net.minecraft.block.BlockNetherWart",
                        "net.minecraft.block.BlockCocoa",
                        "net.minecraft.block.BlockPotato",
                        "net.minecraft.block.BlockNewLeaf",
                        "net.minecraft.block.BlockNewLog",
                        "net.minecraft.block.BlockDoublePlant"
                };
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        System.out.println("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        boolean foundAdded = false;
        boolean foundRemoved = false;

        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals(ADDED) && methodNode.desc.equals(ADDED_DESC))
            {
                foundAdded = true;
                System.out.println("Transforming onBlockAdded");

                InsnList list = new InsnList();

                list.add(new VarInsnNode(AALOAD, 0));
                list.add(new VarInsnNode(AALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockAdded", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false));

                methodNode.instructions.insert(methodNode.instructions.getFirst(), list);
                System.out.println(Arrays.toString(methodNode.instructions.toArray()));
            }
            else if (methodNode.name.equals(REMOVED) && methodNode.desc.equals(REMOVED_DESC))
            {
                foundRemoved = true;
                System.out.println("Transforming breakBlock");

                InsnList list = new InsnList();

                list.add(new VarInsnNode(AALOAD, 0));
                list.add(new VarInsnNode(AALOAD, 1));
                list.add(new VarInsnNode(ILOAD, 2));
                list.add(new VarInsnNode(ILOAD, 3));
                list.add(new VarInsnNode(ILOAD, 4));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "onPlantBlockRemoved", CoreTransformer.isObfurscated() ? "(Laji;Lahb;III)V" : "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)V", false));

                methodNode.instructions.insert(methodNode.instructions.getFirst(), list);
            }
        }

        if (!foundAdded)
        {
            //Override, call super and our method
        }
        if (!foundRemoved)
        {
            //Override, call super and our method
        }

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
