package com.dynious.biota.helper;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class WorldHelper
{
    public static int getLightValue(World world, int x, int y, int z)
    {
        return Math.max(world.getSavedLightValue(EnumSkyBlock.Block, x, y, z), world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z));
    }
}
