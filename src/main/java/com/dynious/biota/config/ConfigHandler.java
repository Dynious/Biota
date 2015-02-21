package com.dynious.biota.config;

import com.dynious.biota.config.annotations.ConfigBooleanValue;
import com.dynious.biota.config.annotations.ConfigDoubleValue;
import com.dynious.biota.config.annotations.ConfigFloatValue;
import com.dynious.biota.config.annotations.ConfigIntValue;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;

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
        for (Field field : Settings.class.getDeclaredFields())
        {
            try
            {
                if (field.isAnnotationPresent(ConfigBooleanValue.class))
                {
                    ConfigBooleanValue annotation = field.getAnnotation(ConfigBooleanValue.class);
                    if (annotation.comment().isEmpty())
                        field.setBoolean(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue()).getBoolean(annotation.defaultValue()));
                    else
                        field.setBoolean(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue(), annotation.comment()).getBoolean(annotation.defaultValue()));
                }
                else if (field.isAnnotationPresent(ConfigIntValue.class))
                {
                    ConfigIntValue annotation = field.getAnnotation(ConfigIntValue.class);
                    if (annotation.comment().isEmpty())
                        field.setInt(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue()).getInt(annotation.defaultValue()));
                    else
                        field.setInt(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue(), annotation.comment()).getInt(annotation.defaultValue()));
                }
                else if (field.isAnnotationPresent(ConfigFloatValue.class))
                {
                    ConfigFloatValue annotation = field.getAnnotation(ConfigFloatValue.class);
                    //Hacky way to prevent float to double conversion from 'losing precision'
                    double defaultValue = Double.parseDouble(Float.toString(annotation.defaultValue()));
                    if (annotation.comment().isEmpty())
                        field.setFloat(null, (float) configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), defaultValue).getDouble(defaultValue));
                    else
                        field.setFloat(null, (float) configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), defaultValue, annotation.comment()).getDouble(defaultValue));
                }
                else if (field.isAnnotationPresent(ConfigDoubleValue.class))
                {
                    ConfigDoubleValue annotation = field.getAnnotation(ConfigDoubleValue.class);
                    if (annotation.comment().isEmpty())
                        field.set(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue()).getDouble(annotation.defaultValue()));
                    else
                        field.set(null, configFile.get(Configuration.CATEGORY_GENERAL, field.getName().replace('_', ' '), annotation.defaultValue(), annotation.comment()).getDouble(annotation.defaultValue()));
                }
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        if (configFile.hasChanged())
            configFile.save();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs)
    {
        if (eventArgs.modID.equals(Reference.MOD_ID))
            ConfigHandler.syncConfig();
    }
}
