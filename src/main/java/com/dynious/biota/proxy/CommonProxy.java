package com.dynious.biota.proxy;

import com.dynious.biota.event.CommonEventHandler;
import com.dynious.biota.event.TerrainEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy
{
    public void initTileEntities()
    {
    }

    public void registerEventHandlers()
    {
        CommonEventHandler ev = new CommonEventHandler();
        FMLCommonHandler.instance().bus().register(ev);
        MinecraftForge.EVENT_BUS.register(ev);
        TerrainEventHandler eventHandler = new TerrainEventHandler();
        MinecraftForge.TERRAIN_GEN_BUS.register(eventHandler);
    }
}
