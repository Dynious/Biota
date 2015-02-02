package com.dynious.biota.creativetab;

import com.dynious.biota.lib.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class CreativeTabBiota extends CreativeTabs
{
    public CreativeTabBiota()
    {
        super(Reference.MOD_ID);
    }

    @Override
    public Item getTabIconItem()
    {
        return Items.apple;
    }
}
