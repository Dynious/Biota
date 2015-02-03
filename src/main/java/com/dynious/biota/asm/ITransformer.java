package com.dynious.biota.asm;

public interface ITransformer
{
    public String[] getClasses();

    public byte[] transform(String transformedName, byte[] clazz);
}
