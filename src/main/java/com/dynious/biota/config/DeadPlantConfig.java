package com.dynious.biota.config;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.lib.BlockAndMeta;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.Map;

public class DeadPlantConfig
{
    //TODO: not a map but our own inplementation to support -1 'wildcard' values
    private static Map<BlockAndMeta, BlockAndMeta> livingDeadMap = new THashMap<BlockAndMeta, BlockAndMeta>();


    public static void setDeadPlant(BlockAndMeta livingPlant, BlockAndMeta deadPlant)
    {
        livingDeadMap.put(livingPlant, deadPlant);
    }

    public static BlockAndMeta getDeadPlant(Block livingPlant, int livingMeta)
    {
        return livingDeadMap.get(new BlockAndMeta(livingPlant, livingMeta));
    }

    public static void init()
    {
        IBiotaAPI.API.registerDeadPlant(Blocks.grass, -1, Blocks.dirt, -1);
        IBiotaAPI.API.registerDeadPlant(Blocks.tallgrass, -1, Blocks.deadbush, -1);
    }
}
