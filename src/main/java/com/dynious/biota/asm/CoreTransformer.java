package com.dynious.biota.asm;

import com.dynious.biota.asm.transformers.ChunkTransformer;
import com.dynious.biota.asm.transformers.PlantTransformer;
import com.dynious.biota.asm.transformers.TreeTransformer;
import net.minecraft.launchwrapper.IClassTransformer;

public class CoreTransformer implements IClassTransformer
{
    private static ITransformer[] transformers;

    static
    {
        transformers = new ITransformer[]
                {
                        new PlantTransformer(),
                        new ChunkTransformer(),
                        new TreeTransformer()
                };
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] clazz)
    {
        for (ITransformer transformer : transformers)
        {
            for (String clazzName : transformer.getClasses())
            {
                if (transformedName.equals(clazzName))
                    clazz = transformer.transform(transformedName, clazz);
            }
        }

        return clazz;
    }
}
