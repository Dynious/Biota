package com.dynious.biota.asm;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASMUtils
{
    public List<MethodNode> getAllMethods(Class clazz)
    {
        List<MethodNode> list = new ArrayList<MethodNode>();

        while (clazz != null)
        {
            ClassNode classNode = ASMUtils.getClassNode(clazz);
            list.addAll(classNode.methods);

            clazz = clazz.getSuperclass();
        }

        return list;
    }

    public static ClassNode getClassNode(Class<?> clazz)
    {
        try
        {
            String name = "/" + clazz.getName().replace(".", "/") + ".class";
            byte[] data = IOUtils.toByteArray(clazz.getResourceAsStream(name));

            ClassNode cnode = new ClassNode();
            ClassReader reader = new ClassReader(data);
            reader.accept(cnode, 0);

            return cnode;
        }
        catch (IOException ignore)
        {
            return null;
        }
    }
}
