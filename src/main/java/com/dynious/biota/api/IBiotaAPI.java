package com.dynious.biota.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IBiotaAPI
{
    public static final IBiotaAPI API = APIGetter.getAPI();

    /**
     * Registers a dead version of the plant. When nutrient values get so low the livingPlant dies it will be replaced
     * with the deadPlant.
     *
     * @param livingPlant The block of the living plant.
     * @param livingMeta The metadata value of the living plant (when this is set to -1 this will be ignored)
     * @param deadPlant The block of the dead plant (replaces the living one).
     * @param deadMeta The metadata value of the dead plant (when livingMeta is set to -1, this will be the same as
     *                 the old meta value was)
     */
    public void registerDeadPlant(Block livingPlant, int livingMeta, Block deadPlant, int deadMeta);

    /**
     * Registers a biomass value for the given block. Ignores metadata values.
     *
     * @param plantBlock The block of the plant
     * @param biomassValue The biomass value
     */
    public void registerPlantValue(Block plantBlock, float biomassValue);

    /**
     * Registers an IPlantSpreader for the given block. You should register the plant value before you register the spreader!
     *
     * @param plantBlock The block of the plant
     * @param plantSpreader The IPlantSpreader
     */
    public void registerPlantSpreader(Block plantBlock, IPlantSpreader plantSpreader);

    /**
     * Registers a biomass value for the given block. Uses the value from the array of the metadata value of the block.
     * If this metadata value is larger than the array size it will use the first value
     *
     * e.g. value = biomassValues[metadata]
     *
     * @param plantBlock The block of the plant
     * @param biomassValues The biomass value of the bock per metadata value
     */
    public void registerPlantValue(Block plantBlock, float[] biomassValues);

    /**
     * When Block#onBlockAdded is called in your plant call this!
     *
     * @param plantBlock The block that was added
     */
    public void onPlantBlockAdded(Block plantBlock, World world, int x, int y, int z);

    /**
     * When Block#breakBlock is called in your plant call this!
     *
     * @param plantBlock The block that was removed
     */
    public void onPlantBlockRemoved(Block plantBlock, World world, int x, int y, int z);

    /**
     * When Block#updateTick is called in your plant call this! Preferably called as soon as possible.
     *
     * @param plantBlock The plant block ticking
     */
    public void onPlantTick(Block plantBlock, World world, int x, int y, int z);

    /**
     * Adds the specified nutrients to the chunk. Used for fertilizers.
     *
     * @param chunk Th chunk the nutrients should get added to (can be found with World#getChunkFromBlockCoords)
     * @param phosphorus The amount of Phosphorus the should be added to the chunk
     * @param potassium The amount of Potassium the should be added to the chunk
     * @param nitrogen The amount of Nitrogen the should be added to the chunk
     * @return If adding the nutrients succeeded
     */
    public boolean addNutrientsToBioSystem(Chunk chunk, float phosphorus, float potassium, float nitrogen);

    /**
     * This will return the color multiplier for your block to change the color when nutrient values get low.
     * Return the value gotten in Block#colorMultiplier when you want your block to change color.
     *
     * @param originalColor The original color multiplier, usually just 0xFFFFFF (can be gotten from super call)
     * @param x The x coordinate of your block
     * @param y The z coordinate of your block
     * @return The color multiplier that should be returned in Block#colorMultiplier
     */
    @SideOnly(Side.CLIENT)
    public int getPlantColorMultiplier(int originalColor, int x, int y);

    static class APIGetter
    {
        public static IBiotaAPI getAPI()
        {
            try
            {
                Class<?> clazz = Class.forName("com.dynious.biota.BiotaAPI");
                return (IBiotaAPI) clazz.getField("INSTANCE").get(null);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }
}
