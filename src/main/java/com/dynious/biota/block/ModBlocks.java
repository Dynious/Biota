package com.dynious.biota.block;

import com.dynious.biota.lib.Names;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockNewGrass grass = new BlockNewGrass();

    public static BlockNitrogenFixingPlant alfafla;
    public static BlockNitrogenFixingPlant clover;
    public static BlockNitrogenFixingPlant fennugreek;
    public static BlockNitrogenFixingPlant lupin;
    public static BlockNitrogenFixingPlant peanut;
    public static BlockNitrogenFixingPlant rooibos;

    public static void init()
    {
        alfafla = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.ALFAFLA);
        clover = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.CLOVER);
        fennugreek = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.FENNUGREEK);
        lupin = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.LUPIN);
        peanut = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.PEANUT);
        rooibos = new BlockNitrogenFixingPlant(0.5F, 1.0F, Names.ROOIBOS);

        GameRegistry.registerBlock(alfafla, Names.ALFAFLA);
        GameRegistry.registerBlock(clover, Names.CLOVER);
        GameRegistry.registerBlock(fennugreek, Names.FENNUGREEK);
        GameRegistry.registerBlock(lupin, Names.LUPIN);
        GameRegistry.registerBlock(peanut, Names.PEANUT);
        GameRegistry.registerBlock(rooibos, Names.ROOIBOS);
    }
}
