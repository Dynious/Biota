package com.dynious.biota.api;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.world.World;

import java.util.Random;

public class DefaultPlantSpreader implements IPlantSpreader
{
    public static final IPlantSpreader INSTANCE = new DefaultPlantSpreader();

    @Override
    public boolean canSpread(World world, int x, int y, int z, Block block)
    {
        //Only spread if canGrow is false (plant is fully grown)
        return !(block instanceof IGrowable) || !((IGrowable) block).func_149851_a(world, x, y, z, false);
    }

    @Override
    public BlockAndMeta spread(World world, int x, int y, int z, Block block)
    {
        Random random = new Random();

        int spreadRange = 2;
        int spreadSize = (2 * spreadRange) + 1;

        int xStart = random.nextInt(spreadSize);
        int yStart = random.nextInt(spreadSize);
        int zStart = random.nextInt(spreadSize);

        for (int dX = 0; dX < spreadSize + 1; dX++)
        {
            for (int dY = 0; dY < spreadSize; dY++)
            {
                for (int dZ = 0; dZ < spreadSize; dZ++)
                {
                    int i = x + ((dX + xStart) % spreadSize) - spreadRange;
                    int j = y + ((dY + yStart) % spreadSize) - spreadRange;
                    int k = z + ((dZ + zStart) % spreadSize) - spreadRange;

                    if (world.isAirBlock(i, j, k) && block.canBlockStay(world, i, j, k))
                    {
                        if (block instanceof IGrowable)
                        {
                            world.setBlock(i, j, k, block, 0, 3);
                            return new BlockAndMeta(block, 0);
                        }
                        else
                        {
                            int meta = world.getBlockMetadata(x, y, z);
                            world.setBlock(i, j, k, block, meta, 3);
                            return new BlockAndMeta(block, meta);
                        }
                    }
                }
            }
        }
        return null;
    }
}
