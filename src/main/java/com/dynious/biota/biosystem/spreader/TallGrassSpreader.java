package com.dynious.biota.biosystem.spreader;

import com.dynious.biota.api.BlockAndMeta;
import com.dynious.biota.api.IPlantSpreader;
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Random;

public class TallGrassSpreader implements IPlantSpreader
{
    @Override
    public boolean canSpread(World world, int x, int y, int z, Block block)
    {
        return true;
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

        for (int dX = 0; dX < spreadSize; dX++)
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
                        int meta = world.getBlockMetadata(x, y, z);
                        world.setBlock(i, j, k, block, meta, 3);
                        return new BlockAndMeta(block, meta);
                    }
                }
            }
        }
        return null;
    }
}
