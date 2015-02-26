package com.dynious.biota.block;

import com.dynious.biota.api.INitrogenFixator;
import net.minecraft.world.World;

public class BlockNitrogenFixingPlant extends BlockBiotaPlant implements INitrogenFixator
{
    private float nitrogenFixationAmount;

    public BlockNitrogenFixingPlant(float biomass, float nitrogenFixationAmount, String name)
    {
        super(biomass);
        this.setBlockName(name);
        this.nitrogenFixationAmount = nitrogenFixationAmount;
    }

    @Override
    public float getNitrogenFixationAmount(World world, int x, int y, int z)
    {
        return nitrogenFixationAmount;
    }
}
