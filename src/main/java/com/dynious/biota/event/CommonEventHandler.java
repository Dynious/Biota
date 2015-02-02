package com.dynious.biota.event;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;

public class CommonEventHandler
{
    @SubscribeEvent
    public void onChuckDataLoad(ChunkDataEvent.Load event)
    {
        //Chunk read from disk
        BioSystemHandler.onChunkLoaded(event.getChunk(), (NBTTagCompound) event.getData().getTag("Biota"));
    }

    @SubscribeEvent
    public void onChuckLoad(ChunkEvent.Load event)
    {
        //Chunk created or read from disk
        if (FMLCommonHandler.instance().getSide().isServer())
            BioSystemHandler.onChunkLoaded(event.getChunk(), null);
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
    }

    @SubscribeEvent
    public void onChuckUnload(ChunkEvent.Unload event)
    {
        //Chunk unloaded
        if (FMLCommonHandler.instance().getSide().isServer())
            BioSystemHandler.onChunkUnload(event.getChunk());
    }
}
