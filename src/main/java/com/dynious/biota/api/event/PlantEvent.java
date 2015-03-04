package com.dynious.biota.api.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class PlantEvent extends Event
{
    public final Block block;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    public final float lowestNutrientValue;
    public final int lightValue;

    private PlantEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
    {
        this.block = block;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.lowestNutrientValue = lowestNutrientValue;
        this.lightValue = lightValue;
    }

    @HasResult
    public static class SpreadEvent extends PlantEvent
    {
        public SpreadEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
        {
            super(block, world, x, y, z, lowestNutrientValue, lightValue);
        }
    }

    @HasResult
    public static class DeathEvent extends PlantEvent
    {
        public DeathEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
        {
            super(block, world, x, y, z, lowestNutrientValue, lightValue);
        }
    }
}
