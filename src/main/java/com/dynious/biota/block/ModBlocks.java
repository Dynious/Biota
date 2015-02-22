package com.dynious.biota.block;

import com.dynious.biota.lib.Names;
import cpw.mods.fml.common.registry.ExistingSubstitutionException;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemBlock;

public class ModBlocks
{
    public static BlockClover blockClover;
    private static BlockNewGrass blockGrass;

    public static void init()
    {
        blockClover = new BlockClover();

        GameRegistry.registerBlock(blockClover, Names.CLOVER);
        try
        {
            GameRegistry.addSubstitutionAlias("minecraft:grass", GameRegistry.Type.BLOCK, getGrassBlock());
            GameRegistry.addSubstitutionAlias("minecraft:grass", GameRegistry.Type.ITEM, new ItemBlock(getGrassBlock()));
        } catch (ExistingSubstitutionException e)
        {
            e.printStackTrace();
        }
    }

    public static BlockNewGrass getGrassBlock()
    {
        if (blockGrass == null)
            blockGrass = new BlockNewGrass();
        return blockGrass;
    }
}
