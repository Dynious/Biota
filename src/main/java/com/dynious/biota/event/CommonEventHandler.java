package com.dynious.biota.event;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.network.NetworkHandler;
import com.dynious.biota.network.message.MessageBioSystemUpdate;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

public class CommonEventHandler
{
    @SubscribeEvent
    public void onChuckDataLoad(ChunkDataEvent.Load event)
    {
        //Chunk read from disk
        NBTTagCompound compound = event.getData().getCompoundTag("Biota");
        BioSystemHandler.onChunkLoaded(event.getChunk(), compound);
    }

    @SubscribeEvent
    public void onChuckLoad(ChunkEvent.Load event)
    {
        if (!event.getChunk().worldObj.isRemote)
        {
            BioSystemHandler.onChunkLoaded(event.getChunk(), null);
        }
    }

    @SubscribeEvent
    public void onChuckDataSave(ChunkDataEvent.Save event)
    {
        //Chunk saved to disk
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.getChunk());
        if (bioSystem != null)
        {
            NBTTagCompound compound = new NBTTagCompound();
            bioSystem.saveToNBT(compound);
            event.getData().setTag("Biota", compound);
        }
        if (!event.getChunk().isChunkLoaded)
            BioSystemHandler.onChunkUnload(event.getChunk());
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            BioSystemHandler.update();
        }
    }

    //TODO: Check if loading & unloading works so no memory leaks will happen
    @SubscribeEvent
    public void onPlayWatchChunk(ChunkWatchEvent.Watch event)
    {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);
        if (bioSystem != null)
        {
            NetworkHandler.INSTANCE.sendTo(new MessageBioSystemUpdate(bioSystem), event.player);
        }
    }
}
