package com.dynious.biota.biosystem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;
import java.util.WeakHashMap;

@SideOnly(Side.CLIENT)
public class ClientBioSystemHandler
{
    public static Map<Chunk, ClientBioSystem> bioSystemMap = new WeakHashMap<Chunk, ClientBioSystem>();
}
