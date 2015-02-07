package com.dynious.biota.asm;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.biosystem.ClientBioSystem;
import com.dynious.biota.biosystem.ClientBioSystemHandler;
import com.dynious.biota.config.DeadPlantConfig;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.lib.BlockAndMeta;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class Hooks
{
    //TODO: Fix bacteria value not setting correctly when new chunks are populized but not calling populizeChunk (WTH?!?!) Only happens outside spawn area. FUUU MC.
    public static void onPlantBlockAdded(Block block, World world, int x, int y, int z)
    {
        /*
        if (BioSystemHandler.isChunkAccessible())
        {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

            if (bioSystem != null)
            {
                bioSystem.addBiomass(PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z)));
            }
        }
        */
        BioSystemHandler.ChunkCoords chunkCoords = new BioSystemHandler.ChunkCoords(world, x >> 4, z >> 4);
        //System.out.println(world + " " + (x >> 4) + " " + (z >> 4));
        float value = PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z));
        BioSystemHandler.changeMap.adjustOrPutValue(chunkCoords, value, value);
    }

    public static void onPlantBlockRemoved(Block block, World world, int x, int y, int z)
    {
        /*
        if (BioSystemHandler.isChunkAccessible())
        {
            Chunk chunk = world.getChunkFromBlockCoords(x, z);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

            if (bioSystem != null)
            {
                bioSystem.addBiomass(-PlantConfig.INSTANCE.getPlantBlockBiomassValue(block, world.getBlockMetadata(x, y, z)));
            }
        }
        */
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
            //TODO: some plants might handle low nutrient values better
            float nutrientValue = bioSystem.getLowestNutrientValue();
            if (false && nutrientValue < Settings.NUTRIENT_SHORTAGE_DEATH)
            {
                //Death to the plants >:c
                int meta = world.getBlockMetadata(x, y, z);
                BlockAndMeta blockAndMeta = DeadPlantConfig.getDeadPlant(block, meta);
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

        return false;
    }

    public static void preChunkPopulated(Chunk chunk)
    {
        /*
        if ((chunk.xPosition) == 0 && (chunk.zPosition) == 0)
            System.out.println("NOW NOT ACCESSIBLE!");
        BioSystemHandler.setDecoratingChunk(true);
        */
    }

    public static void postChunkPopulated(Chunk chunk)
    {
    /*
        if ((chunk.xPosition) == 0 && (chunk.zPosition) == 0)
            System.out.println("NOW ACCESSIBLE!");
        BioSystemHandler.setDecoratingChunk(false);

        BioSystemHandler.onChunkLoaded(chunk, null);
        */

        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);
        if (bioSystem != null)
            BioSystemHandler.stabalizeMap.add(bioSystem);
    }
}
