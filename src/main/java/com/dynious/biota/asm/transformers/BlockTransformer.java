package com.dynious.biota.asm.transformers;

import com.dynious.biota.asm.ITransformer;
import com.dynious.biota.asm.MethodFieldObfHelper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import squeek.asmhelper.applecore.ObfHelper;
import squeek.asmhelper.applecore.ObfRemappingClassWriter;

import java.util.Iterator;

public class BlockTransformer implements ITransformer
{
    private final static String REGISTER = ObfHelper.isObfuscated() ? MethodFieldObfHelper.method("net.minecraft.block.Block", "func_149671_p") : "registerBlocks";
    private final static String REGISTER_DESC = ObfHelper.desc("()V");

    @Override
    public String[] getClasses()
    {
        return new String[] { "net.minecraft.block.Block" };
    }

    @Override
    public byte[] transform(String transformedName, byte[] clazz)
    {
        //Biota.logger.debug("Transforming: " + transformedName);
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(clazz);
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods)
        {
            if (methodNode.name.equals(REGISTER) && methodNode.desc.equals(REGISTER_DESC))
            {
                Iterator<AbstractInsnNode> iterator =  methodNode.instructions.iterator();
                while (iterator.hasNext())
                {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof LdcInsnNode)
                    {
                        if ("grass".equals(((LdcInsnNode) node).cst))
                        {
                            node = node.getNext();
                            ((TypeInsnNode) node).desc = "com/dynious/biota/block/BlockNewGrass";
                            node = node.getNext().getNext();
                            ((MethodInsnNode) node).owner = "com/dynious/biota/block/BlockNewGrass";
                            break;
                        }
                    }
                }
            }
        }

        ClassWriter classWriter = new ObfRemappingClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
