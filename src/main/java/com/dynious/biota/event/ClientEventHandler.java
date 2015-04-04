package com.dynious.biota.event;

import com.dynious.biota.biosystem.ClientBioSystemHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class ClientEventHandler
{
    @SubscribeEvent
    public void onChunkUnloaded(ChunkEvent.Unload event)
    {
        ClientBioSystemHandler.bioSystemMap.remove(event.getChunk());
    }
}
