package com.dynious.biota.api;

import net.minecraft.world.World;

public interface INitrogenFixator extends IPlant
{
    public float getNitrogenFixationAmount(World world, int x, int y, int z);
}
