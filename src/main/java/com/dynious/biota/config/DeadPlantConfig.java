package com.dynious.biota.config;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.lib.BlockAndMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;

public class DeadPlantConfig
{
    private static List<LinkedBlockAndMeta> livingDeadList = new ArrayList<LinkedBlockAndMeta>();


    public static void setDeadPlant(BlockAndMeta livingPlant, BlockAndMeta deadPlant)
    {
        livingDeadList.add(new LinkedBlockAndMeta(livingPlant, deadPlant));
    }

    public static BlockAndMeta getDeadPlant(Block livingPlant, int livingMeta)
    {
        for (LinkedBlockAndMeta linkedBlockAndMeta : livingDeadList)
        {
            if (linkedBlockAndMeta.key.block == livingPlant)
            {
                if (linkedBlockAndMeta.key.meta == livingMeta || linkedBlockAndMeta.key.meta == -1)
                    return linkedBlockAndMeta.value;
            }
        }
        return null;
    }

    public static void init()
    {
        IBiotaAPI.API.registerDeadPlant(Blocks.grass, -1, Blocks.dirt, -1);
        IBiotaAPI.API.registerDeadPlant(Blocks.tallgrass, -1, Blocks.deadbush, -1);
    }

    private static class LinkedBlockAndMeta
    {
        private BlockAndMeta key;
        private BlockAndMeta value;

        public LinkedBlockAndMeta(BlockAndMeta key, BlockAndMeta value)
        {
            this.key = key;
            this.value = value;
        }
    }
}
