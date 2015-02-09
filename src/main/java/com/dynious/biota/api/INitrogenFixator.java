package com.dynious.biota.api;

import net.minecraft.world.World;

public interface INitrogenFixator extends IPlant
{
    /**
     * The amount of nitrogen this nitrogen fixator fixates.
     * Plants will (when not growing) use and return the same amount of nutrients as their biomass.
     * Fixation has the same values as the biomass. When your plant fixates the same amount for nitrogen
     * as the biomass of your plant, your plant uses the same amount of nitrogen as its biomass, but returns
     * twice the amount it uses.
     *
     * @return The amount of nitrogen fixated.
     */
    public float getNitrogenFixationAmount(World world, int x, int y, int z);
}
