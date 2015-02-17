package com.dynious.biota.asm;

import com.dynious.biota.api.BlockAndMeta;
import com.dynious.biota.api.INitrogenFixator;
import com.dynious.biota.api.IPlantSpreader;
import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.biosystem.ClientBioSystem;
import com.dynious.biota.biosystem.ClientBioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.helper.WorldHelper;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Hooks
{
    //TODO: Fix not all biomass added on generation
    public static void onPlantBlockAdded(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = PlantConfig.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
        BioSystemHandler.biomassChangeMap.adjustOrPutValue(chunkCoords, value, value);

        if (block instanceof INitrogenFixator)
        {
            float fixation = ((INitrogenFixator) block).getNitrogenFixationAmount(world, x, y, z);
            BioSystemHandler.nitrogenFixationChangeMap.adjustOrPutValue(chunkCoords, fixation, fixation);
        }
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = -PlantConfig.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
        BioSystemHandler.biomassChangeMap.adjustOrPutValue(chunkCoords, value, value);

        if (block instanceof INitrogenFixator)
        {
            float fixation = -((INitrogenFixator) block).getNitrogenFixationAmount(world, x, y, z);
            BioSystemHandler.nitrogenFixationChangeMap.adjustOrPutValue(chunkCoords, fixation, fixation);
        }
    }

    @SideOnly(Side.CLIENT)
    public static int getColor(int originalColor, int x, int z)
    {
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(x, z);
        ClientBioSystem bioSystem = ClientBioSystemHandler.bioSystemMap.get(chunk);

        if (bioSystem != null)
        {
            return bioSystem.recolorPlants(originalColor);
        }
        return originalColor;
    }

    public static void onPlantTick(Block block, World world, int x, int y, int z)
    {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

        if (bioSystem != null)
        {
            //TODO: some plants might handle low nutrient values better, also curve fit this too, light checking uses 0.05 ms per player loaded area, too much?
            float nutrientValue = bioSystem.getLowestNutrientValue();
            int lightValue = block.isOpaqueCube() ? WorldHelper.getLightValue(world, x, y + 1, z) : WorldHelper.getLightValue(world, x, y, z);
            if (nutrientValue < Settings.NUTRIENT_SHORTAGE_FOR_DEATH || lightValue < Settings.LIGHT_VALUE_FOR_DEATH)
            {
                //Death to the plants >:c
                int meta = world.getBlockMetadata(x, y, z);
                BlockAndMeta blockAndMeta = PlantConfig.getDeadPlant(block, meta);
                if (blockAndMeta != null)
                {
                    if (blockAndMeta.meta == -1)
                    {
                        world.setBlock(x, y, z, blockAndMeta.block, meta, 2);
                    }
                    else
                    {
                        world.setBlock(x, y, z, blockAndMeta.block, blockAndMeta.meta, 2);
                    }
                }
                else
                {
                    world.setBlockToAir(x, y, z);
                }
            }
            else if (nutrientValue > Settings.NUTRIENT_ABUNDANCE_FOR_SPREAD && lightValue > Settings.LIGHT_VALUE_FOR_SPREAD)
            {
                IPlantSpreader spreader = PlantConfig.getPlantSpreader(block);
                if (spreader != null && spreader.canSpread(world, x, y, z, block) && world.rand.nextFloat() < Settings.PLANT_SPREAD_CHANCE)
                {
                    BlockAndMeta blockAndMeta = spreader.spread(world, x, y, z, block);
                    if (blockAndMeta != null)
                    {
                        bioSystem.onGrowth(PlantConfig.getPlantBlockBiomassValue(blockAndMeta.block, blockAndMeta.meta), false);
                    }
                }
            }
        }
    }

    public static void postChunkPopulated(Chunk chunk)
    {
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);
        if (bioSystem != null)
            BioSystemHandler.stabilizeList.add(bioSystem);
    }

    public static void onPlantGrowth(World world, int x, int y, int z, Block block, int meta)
    {
        float biomass = PlantConfig.getPlantBlockBiomassValue(block, meta);
        if (biomass != 0F)
        {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

            if (bioSystem != null)
            {
                bioSystem.onGrowth(biomass, false);
            }
        }
    }
}
