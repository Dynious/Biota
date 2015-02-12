package com.dynious.biota;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.asm.Hooks;
import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.lib.BlockAndMeta;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BiotaAPI implements IBiotaAPI
{
    public static final IBiotaAPI INSTANCE = new BiotaAPI();

    @Override
    public void registerDeadPlant(Block livingPlant, int livingMeta, Block deadPlant, int deadMeta)
    {
        PlantConfig.registerDeadPlant(new BlockAndMeta(livingPlant, livingMeta), new BlockAndMeta(deadPlant, deadMeta));
    }

    @Override
    public void registerPlantValue(Block plantBlock, float biomassValue)
    {
        if (plantBlock != null)
            PlantConfig.registerPlantValue(plantBlock.getClass(), new float[] { biomassValue });
    }

    @Override
    public void registerPlantValue(Block plantBlock, float[] biomassValues)
    {
        if (plantBlock != null && biomassValues != null && biomassValues.length > 0)
            PlantConfig.registerPlantValue(plantBlock.getClass(), biomassValues);
    }

    @Override
    public void onPlantBlockAdded(Block plantBlock, World world, int x, int y, int z)
    {
        Hooks.onPlantBlockAdded(plantBlock, world, x, y, z);
    }

    @Override
    public void onPlantBlockRemoved(Block plantBlock, World world, int x, int y, int z)
    {
        Hooks.onPlantBlockRemoved(plantBlock, world, x, y, z);
    }

    @Override
    public void onPlantTick(Block plantBlock, World world, int x, int y, int z)
    {
        Hooks.onPlantTick(plantBlock, world, x, y, z);
    }

    @Override
    public boolean addNutrientsToBioSystem(Chunk chunk, float phosphorus, float potassium, float nitrogen)
    {
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);
        if (bioSystem != null)
        {
            bioSystem.setPhosphorus(bioSystem.getPhosphorus() + phosphorus);
            bioSystem.setPotassium(bioSystem.getPotassium() + potassium);
            bioSystem.setNitrogen(bioSystem.getNitrogen() + nitrogen);
            return true;
        }
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getPlantColorMultiplier(int originalColor, int x, int y)
    {
        return Hooks.getColor(originalColor, x, y);
    }
}
