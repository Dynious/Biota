package com.dynious.biota.block;

import com.dynious.biota.lib.Names;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockClover blockClover;

    public static void init()
    {
        blockClover = new BlockClover();

        GameRegistry.registerBlock(blockClover, Names.CLOVER);
    }
}
