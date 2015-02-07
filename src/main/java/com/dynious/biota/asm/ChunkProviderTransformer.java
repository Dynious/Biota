package com.dynious.biota.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class ChunkProviderTransformer implements ITransformer
{
    private final static String LOAD = "originalLoadChunk";
    private final static String LOAD_DESC = CoreTransformer.isObfurscated() ? "(II)Lapx;" : "(II)Lnet/minecraft/world/chunk/Chunk;";

    private final static String CHUNK_LOAD = CoreTransformer.isObfurscated() ? "c" : "onChunkLoad";
    private final static String CHUNK_LOAD_DESC = "()V";

    private final static String POPULATE = CoreTransformer.isObfurscated() ? "a" : "populateChunk";
    private final static String POPULATE_DESC = CoreTransformer.isObfurscated() ? "(Lapu;Lapu;II)V" : "(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V";

    @Override
    public String[] getClasses()
    {
        return new String[] { "net.minecraft.world.gen.ChunkProviderServer"};
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        System.out.println("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals(LOAD) && methodNode.desc.equals(LOAD_DESC))
            {
                MethodInsnNode chunkLoadNode = null;
                MethodInsnNode populateNode = null;

                for (Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); )
                {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof MethodInsnNode)
                    {
                        MethodInsnNode aNode = (MethodInsnNode) node;
                        if (aNode.name.equals(CHUNK_LOAD) && aNode.desc.equals(CHUNK_LOAD_DESC))
                        {
                            chunkLoadNode = aNode;
                        }
                        else if (aNode.name.equals(POPULATE) && aNode.desc.equals(POPULATE_DESC))
                        {
                            populateNode = aNode;
                        }
                    }
                }

                if (chunkLoadNode != null)
                {
                    InsnList list = new InsnList();

                    list.add(new VarInsnNode(ALOAD, 5));
                    list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "preChunkPopulated", CoreTransformer.isObfurscated() ? "(Lapx;)V" : "(Lnet/minecraft/world/chunk/Chunk;)V", false));

                    methodNode.instructions.insert(chunkLoadNode, list);
                }
                if (populateNode != null)
                {
                    InsnList list = new InsnList();

                    list.add(new VarInsnNode(ALOAD, 5));
                    list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "postChunkPopulated", CoreTransformer.isObfurscated() ? "(Lapx;)V" : "(Lnet/minecraft/world/chunk/Chunk;)V", false));

                    methodNode.instructions.insert(populateNode, list);
                }

                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
