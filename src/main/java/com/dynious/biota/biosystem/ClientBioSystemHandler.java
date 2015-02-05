package com.dynious.biota.biosystem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.hash.THashMap;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientBioSystemHandler
{
    public static Map<Chunk, ClientBioSystem> bioSystemMap = new THashMap<Chunk, ClientBioSystem>();
}
