package com.dynious.biota;

import com.dynious.biota.config.ConfigHandler;
import com.dynious.biota.creativetab.CreativeTabBiota;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES, guiFactory = "com.dynious.biota.config.GuiFactory")
public class Biota
{
    @Mod.Instance(Reference.MOD_ID)
    public static Biota instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    public static CreativeTabs tabBiota = new CreativeTabBiota();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event);

        /*
        ModBlocks.init();

        ModItems.init();
        */

        proxy.registerEventHandlers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.initTileEntities();
    }
}
