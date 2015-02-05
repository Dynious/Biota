package com.dynious.biota.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;

public class ClientEventHandler
{
    @SubscribeEvent
    public void grassColor(BiomeEvent.GetGrassColor grassColor)
    {
        //We need the coords ;_;
        //grassColor.biome;
    }
}
