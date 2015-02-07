package com.dynious.biota.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.IOException;

public class CoreTransformer implements IClassTransformer
{
    private static ITransformer[] transformers;
    private static Boolean obfuscated = null;

    static
    {
        transformers = new ITransformer[]
                {
                        new PlantTransformer(),
                        new ChunkTransformer()
                };
    }

    public static boolean isObfurscated()
    {
        if (obfuscated == null)
        {
            try
            {
                byte[] bytes = ((LaunchClassLoader) CoreTransformer.class.getClassLoader()).getClassBytes("net.minecraft.world.World");
                obfuscated = bytes == null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return obfuscated;
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
