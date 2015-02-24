package com.dynious.biota.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import squeek.asmhelper.applecore.ObfHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MethodFieldObfHelper
{
    private static Method getFieldMap;
    private static Method getMethodMap;

    static
    {
        try
        {
            getFieldMap = FMLDeobfuscatingRemapper.class.getDeclaredMethod("getFieldMap", String.class);
            getFieldMap.setAccessible(true);
            getMethodMap = FMLDeobfuscatingRemapper.class.getDeclaredMethod("getMethodMap", String.class);
            getMethodMap.setAccessible(true);
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    public static String field(String deobfClassName, String deobfFieldName)
    {
        if (!ObfHelper.isObfuscated())
            return deobfFieldName;
        try
        {
            Map<String, String> map = (Map<String, String>) getFieldMap.invoke(FMLDeobfuscatingRemapper.INSTANCE, ObfHelper.forceToObfClassName(deobfClassName));

            if (map != null)
            {
                for (Map.Entry<String, String> e : map.entrySet())
                {
                    if (deobfFieldName.equals(e.getValue()))
                    {
                        return e.getKey().substring(0, e.getKey().indexOf(':'));
                    }
                }
            }
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return deobfFieldName;
    }

    public static String method(String deobfClassName, String deobfMethodName)
    {
        if (!ObfHelper.isObfuscated())
            return deobfMethodName;
        try
        {
            Map<String, String> map = (Map<String, String>) getMethodMap.invoke(FMLDeobfuscatingRemapper.INSTANCE, ObfHelper.forceToObfClassName(deobfClassName));

            if (map != null)
            {
                for (Map.Entry<String, String> e : map.entrySet())
                {
                    if (deobfMethodName.equals(e.getValue()))
                    {
                        return e.getKey().substring(0, e.getKey().indexOf('('));
                    }
                }
            }
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        } catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        return deobfMethodName;
    }
}
