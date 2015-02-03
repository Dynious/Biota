package com.dynious.biota.asm;

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
        System.out.println("Plant block added in " + world.toString() + " at: " + x + ", " + y + ", " + z);
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        System.out.println("Plant block removed in " + world.toString() + " at: " + x + ", " + y + ", " + z);
    }
}
