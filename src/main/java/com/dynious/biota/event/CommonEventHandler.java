package com.dynious.biota.event;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.Iterator;

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
            Iterator<BioSystem> iterator = BioSystemHandler.iterator();
            while (iterator.hasNext())
            {
                iterator.next().update();
            }
        }
    }
}
