package com.dynious.biota.block;

import com.dynious.biota.asm.Hooks;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import squeek.applecore.api.AppleCoreAPI;

import java.util.Random;

public class BlockNewGrass extends BlockGrass
{
    public BlockNewGrass()
    {
        this.setHardness(0.6F);
        this.setStepSound(soundTypeGrass);
        this.setBlockName("grass");
        this.setBlockTextureName("grass");
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random)
    {
        Hooks.onPlantTick(this, world, x, y, z);
        if (!world.isRemote)
        {
            if (world.getBlockLightValue(x, y + 1, z) < 4 && world.getBlockLightOpacity(x, y + 1, z) > 2)
            {
                world.setBlock(x, y, z, Blocks.dirt);
            }
            else
            {
                Event.Result result = AppleCoreAPI.dispatcher.validatePlantGrowth(this, world, x, y, z, random);
                if (result == Event.Result.ALLOW || (result == Event.Result.DEFAULT && world.getBlockLightValue(x, y + 1, z) >= 9))
                {
                    int meta = world.getBlockMetadata(x, y, z);
                    if (meta == 0)
                    {
                        for (int l = 0; l < 4; ++l)
                        {
                            int i1 = x + random.nextInt(3) - 1;
                            int j1 = y + random.nextInt(5) - 3;
                            int k1 = z + random.nextInt(3) - 1;

                            if (world.getBlock(i1, j1, k1) == Blocks.dirt && world.getBlockMetadata(i1, j1, k1) == 0 && world.getBlockLightValue(i1, j1 + 1, k1) >= 4 && world.getBlockLightOpacity(i1, j1 + 1, k1) <= 2)
                            {
                                world.setBlock(i1, j1, k1, Blocks.grass, 15, 3);
                                Hooks.onPlantGrowth(world, i1, j1, k1, this, 15);
                            }
                        }
                    }
                    else if (random.nextInt(Settings.GRASS_GROW_BACK_CHANCE) == 0)
                    {
                        world.setBlockMetadataWithNotify(x, y, z, meta - 1, 3);
                        AppleCoreAPI.dispatcher.announcePlantGrowth(this, world, x, y, z, meta);
                    }
                }
            }
        }
    }

    @Override
    public void onEntityWalking(World world, int x, int y, int z, Entity entity)
    {
        Random rand = world.rand;
        if (rand.nextInt(Settings.GRASS_WORN_ENTITY_WALK_ON_CHANCE) == 0)
        {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta < 15)
            {
                world.setBlockMetadataWithNotify(x, y, z, meta + 1, 3);
            }
            else
            {
                world.setBlock(x, y, z, Blocks.dirt);
            }
        }
        super.onEntityWalking(world, x, y, z, entity);
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        int color = super.colorMultiplier(world, x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        int r = (color & 0xFFFFFF) >> 16;
        int g = (color & 0xFFFF) >> 8;
        int b = color & 0xFF;

        r = ((15 - meta)*r + meta*220)/15;
        g = ((15 - meta)*g + meta*163)/15;
        b = ((15 - meta)*b + meta*118)/15;

        return (r & 255) << 16 | (g & 255) << 8 | b & 255;
    }
}
