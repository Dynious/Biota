package com.dynious.biota.asm;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.biosystem.ClientBioSystem;
import com.dynious.biota.biosystem.ClientBioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Hooks
{
    public static boolean shouldStopUpdate(Block block)
    {
        System.out.println(block);
        return false;
    }

    public static void onPlantBlockAdded(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
        BioSystemHandler.changeMap.adjustOrPutValue(chunkCoords, value, value);
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        float value = -PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
        BioSystemHandler.changeMap.adjustOrPutValue(chunkCoords, value, value);
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

    /**
     *
     * @return Stop update tick.
     */
    public static boolean onPlantTick(Block block, World world, int x, int y, int z)
    {
        Chunk chunk = world.getChunkFromBlockCoords(x, z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

        if (bioSystem != null)
        {
            //TODO: dead plants, not just disappear, also some plants might handle low nutrient values better
            float nutrientValue = bioSystem.getLowestNutrientValue();
            if (nutrientValue < 0.35)
            {
                //Death to the plants >:c
                //Special case for grass for now
                if (block instanceof BlockGrass)
                {
                    world.setBlock(x, y, z, Blocks.dirt);
                }
                else
                {
                    world.setBlockToAir(x, y, z);
                }
            }

            if (nutrientValue < 0.75)
            {
                //No longer grow
                return true;
            }
        }

        return false;
    }
}
