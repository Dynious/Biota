package com.dynious.biota.block;

import com.dynious.biota.api.INitrogenFixator;
import com.dynious.biota.lib.Names;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockClover extends BlockBiota implements INitrogenFixator
{
    public BlockClover()
    {
        super(Material.plants);
        this.setBlockName(Names.CLOVER);
        this.setAsPlant(0.5F);
        this.setTickRandomly(true);
    }

    @Override
    public float getNitrogenFixationAmount(World world, int x, int y, int z)
    {
        return 1F;
    }
}
