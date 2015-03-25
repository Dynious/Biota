package com.dynious.biota.asm;

import com.dynious.biota.api.BlockAndMeta;
import com.dynious.biota.api.INitrogenFixator;
import com.dynious.biota.api.IPlantSpreader;
import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.biosystem.ClientBioSystem;
import com.dynious.biota.biosystem.ClientBioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.event.EventPoster;
import com.dynious.biota.helper.WorldHelper;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.common.eventhandler.Event;
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
        BioSystemHandler handler = BioSystemHandler.get(world);
        if (handler != null)
        {
            BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
            float value = PlantConfig.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
            handler.biomassChangeMap.adjustOrPutValue(chunkCoords, value, value);

            if (block instanceof INitrogenFixator)
            {
                float fixation = ((INitrogenFixator) block).getNitrogenFixationAmount(world, x, y, z);
                handler.nitrogenFixationChangeMap.adjustOrPutValue(chunkCoords, fixation, fixation);
            }
        }
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler handler = BioSystemHandler.get(world);
        if (handler != null)
        {
            BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
            float value = -PlantConfig.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
            handler.biomassChangeMap.adjustOrPutValue(chunkCoords, value, value);

            if (block instanceof INitrogenFixator)
            {
                float fixation = -((INitrogenFixator) block).getNitrogenFixationAmount(world, x, y, z);
                handler.nitrogenFixationChangeMap.adjustOrPutValue(chunkCoords, fixation, fixation);
            }
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
        world.theProfiler.startSection("plantTick");
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(world, chunk);

        if (bioSystem != null)
        {
            //TODO: Curve fit this too, light checking uses 0.05 ms per player loaded area, too much?
            int meta = world.getBlockMetadata(x, y, z);

            float nutrientValue = PlantConfig.getLowestNutrientPart(block, meta, bioSystem.getPhosphorus(), bioSystem.getPotassium(), bioSystem.getNitrogen());
            int lightValue = block.isOpaqueCube() ? WorldHelper.getLightValue(world, x, y + 1, z) : WorldHelper.getLightValue(world, x, y, z);

            Event.Result spreadResult = EventPoster.postSpreadEvent(block, world, x, y, z, nutrientValue, lightValue);
            if (spreadResult == Event.Result.ALLOW || (spreadResult == Event.Result.DEFAULT && nutrientValue > Settings.NUTRIENT_AMOUNT_FOR_SPREAD && lightValue > Settings.LIGHT_VALUE_FOR_SPREAD))
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

            Event.Result deathResult = EventPoster.postDeathEvent(block, world, x, y, z, nutrientValue, lightValue);
            if (deathResult == Event.Result.ALLOW || (deathResult == Event.Result.DEFAULT && (nutrientValue < Settings.NUTRIENT_AMOUNT_FOR_DEATH || lightValue < Settings.LIGHT_VALUE_FOR_DEATH)))
            {
                //Death to the plants >:c
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
        }
        world.theProfiler.endSection();
    }

    public static void postChunkPopulated(Chunk chunk)
    {
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk.worldObj, chunk);
        if (bioSystem != null)
            BioSystemHandler.get(chunk.worldObj).stabilizeList.add(bioSystem);
    }

    public static void onPlantGrowth(World world, int x, int y, int z, Block block, int meta)
    {
        float biomass = PlantConfig.getPlantBlockBiomassValue(block, meta);
        if (biomass != 0F)
        {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(world, chunk);

            if (bioSystem != null)
            {
                bioSystem.onGrowth(biomass, false);
            }
        }
    }
}
