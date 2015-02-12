package com.dynious.biota.block;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.api.INitrogenFixator;
import com.dynious.biota.lib.Names;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockClover extends BlockBiotaPlant implements INitrogenFixator
{
    public BlockClover()
    {
        super(0.5F);
        this.setBlockName(Names.CLOVER);
    }

    @Override
    public float getNitrogenFixationAmount(World world, int x, int y, int z)
    {
        return 1F;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        int original = world.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
        return IBiotaAPI.API.getPlantColorMultiplier(original, x, z);
    }
}
