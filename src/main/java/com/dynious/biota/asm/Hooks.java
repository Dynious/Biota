package com.dynious.biota.asm;

import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class Hooks
{
    public static boolean shouldStopUpdate(Block block)
    {
        System.out.println(block);
        return false;
    }

    public static void onPlantBlockAdded(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = PlantConfig.INSTANCE.getPlantBlockBiomassValue(block);
        BioSystemHandler.changeMap.adjustOrPutValue(chunkCoords, value, value);
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = -PlantConfig.INSTANCE.getPlantBlockBiomassValue(block);
        BioSystemHandler.changeMap.adjustOrPutValue(chunkCoords, value, value);
    }
}
