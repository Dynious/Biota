package com.dynious.biota.biosystem;

import com.dynious.biota.config.PlantConfig;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class BioSystemInitThread implements Callable
{
    private static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private final BioSystem bioSystem;

    public BioSystemInitThread(BioSystem bioSystem)
    {
        this.bioSystem = bioSystem;
    }

    @Override
    public Object call()
    {
        float biomass = 0F;
        Chunk chunk = bioSystem.chunkReference.get();

        if (chunk != null)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int y = 0; y < 256; y++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        Block block = chunk.getBlock(x, y, z);
                        if (block instanceof IPlant)
                        {
                            int meta = chunk.getBlockMetadata(x, y, z);
                            biomass += PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, meta);
                        }
                    }
                }
            }
            bioSystem.setBiomass(biomass);
        }

        return null;
    }

    public static void addBioSystem(BioSystem bioSystem)
    {
        listeningExecutorService.submit(new BioSystemInitThread(bioSystem));
    }
}
