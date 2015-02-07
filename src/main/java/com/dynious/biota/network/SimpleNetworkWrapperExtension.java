package com.dynious.biota.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.world.WorldServer;

import java.util.List;

public class SimpleNetworkWrapperExtension extends SimpleNetworkWrapper
{
    public SimpleNetworkWrapperExtension(String channelName)
    {
        super(channelName);
    }

    public void sendToPlayersWatchingChunk(IMessage message, WorldServer worldServer, int chunkX, int chunkZ)
    {
        PlayerManager.PlayerInstance pi = worldServer.getPlayerManager().getOrCreateChunkWatcher(chunkX, chunkZ, false);
        if (pi != null)
        {
            for (EntityPlayerMP player : (List<EntityPlayerMP>) pi.playersWatchingChunk)
            {
                sendTo(message, player);
            }
        }
    }
}
