package com.dynious.biota.biosystem;

import gnu.trove.map.hash.THashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Map;

public class BioSystemHandler
{
    private static Map<Chunk, BioSystem> bioSystemMap = new THashMap<Chunk, BioSystem>();

    public static void onChunkLoaded(Chunk chunk, NBTTagCompound compound)
    {
        assert chunk != null;

        BioSystem bioSystem;

        if (compound == null)
        {
            if (bioSystemMap.containsKey(chunk))
                return;

            bioSystem = new BioSystem(chunk);
        }
        else
        {
            bioSystem = BioSystem.loadFromNBT(chunk, compound);
        }

        bioSystemMap.put(chunk, bioSystem);
        if (chunk.xPosition == 0 && chunk.zPosition == 0)
            System.out.println(bioSystem.toString());
    }

    public static void onChunkUnload(Chunk chunk)
    {
        bioSystemMap.remove(chunk);
    }

    public static BioSystem getBioSystem(Chunk chunk)
    {
        return bioSystemMap.get(chunk);
    }

    public static Iterator<BioSystem> iterator()
    {
        return bioSystemMap.values().iterator();
    }
}
