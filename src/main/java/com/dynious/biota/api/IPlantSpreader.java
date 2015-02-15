package com.dynious.biota.api;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IPlantSpreader
{
    /**
     * This method will be called when your plant can spread to nearby blocks.
     * Light- and nutrient values are checked by Biota, but your plant might
     * only spread on a certain metadata value or another condition.
     *
     * @param world The World.
     * @param x The x position of the plant that wants to spread.
     * @param y The y position of the plant that wants to spread.
     * @param z The z position of the plant that wants to spread.
     * @param block The Block instance of the plant that wants to spread.
     * @return If the plant can spread.
     */
    public boolean canSpread(World world, int x, int y, int z, Block block);

    /**
     * This method will be called when your plant should spread to nearby blocks.
     *
     * @param world The World.
     * @param x The x position of the plant that wants to spread.
     * @param y The y position of the plant that wants to spread.
     * @param z The z position of the plant that wants to spread.
     * @param block The Block instance of the plant that wants to spread.
     * @return The Block and its meta value added to the world. Null if spreading failed.
     */
    public BlockAndMeta spread(World world, int x, int y, int z, Block block);
}
