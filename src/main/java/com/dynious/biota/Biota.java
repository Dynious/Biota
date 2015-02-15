package com.dynious.biota;

import com.dynious.biota.block.ModBlocks;
import com.dynious.biota.command.CommandBiota;
import com.dynious.biota.config.ConfigHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.creativetab.CreativeTabBiota;
import com.dynious.biota.item.ModItems;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.network.NetworkHandler;
import com.dynious.biota.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.creativetab.CreativeTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES, guiFactory = "com.dynious.biota.config.GuiFactory")
public class Biota
{
    @Mod.Instance(Reference.MOD_ID)
    public static Biota instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
    public static CommonProxy proxy;

    public static CreativeTabs tabBiota = new CreativeTabBiota();
    public static Logger logger = LogManager.getLogger(Reference.MOD_ID);

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandBiota());
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHandler.init(event);

        ModBlocks.init();

        ModItems.init();

        PlantConfig.init();

        NetworkHandler.init();

        proxy.registerEventHandlers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.initTileEntities();
    }
}
