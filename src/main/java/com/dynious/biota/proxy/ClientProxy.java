package com.dynious.biota.proxy;

import com.dynious.biota.event.ClientEventHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
    @Override
    public void initTileEntities()
    {
        super.initTileEntities();

    }

    @Override
    public void registerEventHandlers()
    {
        super.registerEventHandlers();
        ClientEventHandler ev = new ClientEventHandler();
        FMLCommonHandler.instance().bus().register(ev);
        MinecraftForge.EVENT_BUS.register(ev);
    }
}
