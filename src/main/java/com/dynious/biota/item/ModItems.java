package com.dynious.biota.item;

import com.dynious.biota.lib.Names;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ModItems
{
    public static Item potash;

    public static void init()
    {
        potash = new ItemPotash();

        GameRegistry.registerItem(potash, Names.POTASH);
    }
}
