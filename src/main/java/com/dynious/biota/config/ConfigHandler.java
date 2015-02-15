package com.dynious.biota.config;

import com.dynious.biota.lib.Reference;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler
{
    public static Configuration configFile;

    public static void init(FMLPreInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(new ConfigHandler());
        configFile = new Configuration(new File(event.getModConfigurationDirectory(),  Reference.MOD_ID.toLowerCase() + File.separator + "general.cfg"));
        syncConfig();
    }

    public static void syncConfig()
    {

        if (configFile.hasChanged())
            configFile.save();
    }

    private static int getConfiguration(String setting, int defaultSetting, String comment)
    {
        return configFile.get(Configuration.CATEGORY_GENERAL, setting, defaultSetting, comment).getInt(defaultSetting);
    }

    private static boolean getConfiguration(String setting, boolean defaultSetting, String comment)
    {
        return configFile.get(Configuration.CATEGORY_GENERAL, setting, defaultSetting, comment).getBoolean(defaultSetting);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
    {
        if (eventArgs.modID.equals(Reference.MOD_ID))
            ConfigHandler.syncConfig();
    }
}
