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

    /**
     * Fired every block tick of a plant. This determines if a plant will spread.
     *
     * This event is not {@link cpw.mods.fml.common.eventhandler.Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will only spread when Biotas conditions are met.
     * {@link Result#ALLOW} will allow spread without condition.
     * {@link Result#DENY} will deny spread without condition.
     */
    @HasResult
    public static class SpreadEvent extends PlantEvent
    {
        public SpreadEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
        {
            super(block, world, x, y, z, lowestNutrientValue, lightValue);
        }
    }

    /**
     * Fired every block tick of a plant. This determines if a plant will die.
     *
     * This event is not {@link cpw.mods.fml.common.eventhandler.Cancelable}.
     *
     * This event uses the {@link Result}. {@link HasResult}
     * {@link Result#DEFAULT} will only die when Biotas conditions are met.
     * {@link Result#ALLOW} will allow death without condition.
     * {@link Result#DENY} will deny death without condition.
     */
    @HasResult
    public static class DeathEvent extends PlantEvent
    {
        public DeathEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
        {
            super(block, world, x, y, z, lowestNutrientValue, lightValue);
        }
    }
}
