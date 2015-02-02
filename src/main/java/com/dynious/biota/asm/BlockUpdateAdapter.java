package com.dynious.biota.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class BlockUpdateAdapter extends AdviceAdapter
{
    /**
     * Creates a new {@link org.objectweb.asm.commons.AdviceAdapter}.
     *
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param name   the method's name.
     * @param desc   the method's descriptor (see {@link org.objectweb.asm.Type Type}).
     */
    protected BlockUpdateAdapter(MethodVisitor mv, String name, String desc)
    {
        //updateTick
        super(ASM5, mv, ACC_PUBLIC, name, desc);
    }

    @Override
    protected void onMethodEnter()
    {
        visitInsn(RETURN);
    }
}
