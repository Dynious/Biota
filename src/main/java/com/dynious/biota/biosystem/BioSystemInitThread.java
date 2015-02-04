package com.dynious.biota.biosystem;

import com.dynious.biota.config.PlantConfig;
import net.minecraft.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BioSystemInitThread extends Thread
{
    public static final BioSystemInitThread INSTANCE = new BioSystemInitThread();

    private List<BioSystem> toDo = new ArrayList<BioSystem>();
    private boolean run = true;

    private BioSystemInitThread()
    {
        super("BioSystemInit");
        setPriority(4);
    }

    @Override
    public void run()
    {
        while(run)
        {
            if (!toDo.isEmpty())
            {
                float biomass = 0F;
                BioSystem bioSystem = toDo.get(0);

                for (int x = 0; x < 16; x++)
                {
                    for (int y = 0; y < 256; y++)
                    {
                        for (int z = 0; z < 16; z++)
                        {
                            Block block = bioSystem.chunk.getBlock(x, y, z);
                            if (block instanceof IPlant)
                            {
                                biomass += PlantConfig.INSTANCE.getPlantBlockBiomassValue(block);
                            }
                        }
                    }
                }
                bioSystem.setBiomass(biomass);

                toDo.remove(bioSystem);
            }
            else
            {
                try
                {
                    join(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void addBioSystem(BioSystem bioSystem)
    {
        if (bioSystem != null)
            toDo.add(bioSystem);
        if (!this.isAlive())
            this.start();
    }
}
