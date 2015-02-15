package com.dynious.biota.asm;

import com.dynious.biota.Biota;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static org.objectweb.asm.Opcodes.*;

public class ChunkTransformer implements ITransformer
{
    private final static String POPULATE = CoreTransformer.isObfurscated() ? "a" : "populateChunk";
    private final static String POPULATE_DESC = CoreTransformer.isObfurscated() ? "(Lapu;Lapu;II)V" : "(Lnet/minecraft/world/chunk/IChunkProvider;Lnet/minecraft/world/chunk/IChunkProvider;II)V";

    @Override
    public String[] getClasses()
    {
        return new String[] { "net.minecraft.world.chunk.Chunk"};
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        Biota.logger.debug("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals(POPULATE) && methodNode.desc.equals(POPULATE_DESC))
            {
                InsnList list = new InsnList();

                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new MethodInsnNode(INVOKESTATIC, Type.getInternalName(Hooks.class), "postChunkPopulated", CoreTransformer.isObfurscated() ? "(Lapx;)V" : "(Lnet/minecraft/world/chunk/Chunk;)V", false));


                for (Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator(); iterator.hasNext(); )
                {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == RETURN)
                    {
                        methodNode.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }

        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
