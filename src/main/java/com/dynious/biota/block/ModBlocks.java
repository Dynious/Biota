package com.dynious.biota.block;

import com.dynious.biota.lib.Names;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockNitrogenFixingPlant[] plants = new BlockNitrogenFixingPlant[6];

    public static void init()
    {
        plants[0] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.ALFAFLA);
        plants[1] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.CLOVER);
        plants[2] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.FENUGREEK);
        plants[3] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.LUPIN);
        plants[4] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.PEANUT);
        plants[5] = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.ROOIBOS);

        GameRegistry.registerBlock(plants[0], Names.ALFAFLA);
        GameRegistry.registerBlock(plants[1], Names.CLOVER);
        GameRegistry.registerBlock(plants[2], Names.FENUGREEK);
        GameRegistry.registerBlock(plants[3], Names.LUPIN);
        GameRegistry.registerBlock(plants[4], Names.PEANUT);
        GameRegistry.registerBlock(plants[5], Names.ROOIBOS);
    }
}
