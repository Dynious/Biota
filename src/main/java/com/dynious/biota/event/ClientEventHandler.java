package com.dynious.biota.event;

import com.dynious.biota.biosystem.ClientBioSystemHandler;
import com.dynious.biota.block.ModBlocks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class ClientEventHandler
{
    @SubscribeEvent
    public void onChunkUnloaded(ChunkEvent.Unload event)
    {
        ClientBioSystemHandler.bioSystemMap.remove(event.getChunk());
    }

    @SubscribeEvent
    public void textureEvent(TextureStitchEvent.Pre event)
    {
        if (event.map.getTextureType() == 0)
            ModBlocks.grass.registerBlockIcons(event.map);
    }
}
