package com.dynious.biota.creativetab;

import com.dynious.biota.block.ModBlocks;
import com.dynious.biota.lib.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabBiota extends CreativeTabs
{
    public CreativeTabBiota()
    {
        super(Reference.MOD_ID);
    }

    @Override
    public Item getTabIconItem()
    {
        return null;
    }

    @Override
    public ItemStack getIconItemStack()
    {
        return new ItemStack(ModBlocks.clover);
    }
}
