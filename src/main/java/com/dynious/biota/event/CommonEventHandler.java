package com.dynious.biota.event;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.asm.Hooks;
import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.helper.WorldHelper;
import com.dynious.biota.lib.MathLib;
import com.dynious.biota.lib.Settings;
import com.dynious.biota.network.NetworkHandler;
import com.dynious.biota.network.message.MessageBioSystemUpdate;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import squeek.applecore.api.plants.FertilizationEvent;
import squeek.applecore.api.plants.PlantGrowthEvent;

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
            BioSystemHandler.onChunkLoaded(event.getChunk());
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

    @SubscribeEvent
    public void allowPlantGrowth(PlantGrowthEvent.AllowGrowthTick event)
    {
        Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

        if (bioSystem != null)
        {
            float nutrientGrowChance = MathLib.getFittedValue(bioSystem.getLowestNutrientValue(), Settings.NUTRIENT_SHORTAGE_FOR_STOP_GROWTH, Settings.NUTRIENT_AMOUNT_FOR_NORMAL_GROWTH, Settings.NUTRIENT_ABUNDANCE_FOR_MAX_GROWTH);
            float lightGrowChance = MathLib.getFittedValue(WorldHelper.getLightValue(event.world, event.x, event.y, event.z), Settings.LIGHT_VALUE_FOR_STOP_GROWTH, Settings.LIGHT_VALUE_FOR_NORMAL_GROWTH, Settings.LIGHT_VALUE_FOR_MAX_GROWTH);

            if (Math.min(nutrientGrowChance, lightGrowChance) < event.world.rand.nextFloat())
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void onPlantGrowth(PlantGrowthEvent.GrowthTick event)
    {
        Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

        if (bioSystem != null)
        {
            int newMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
            float biomassChange = PlantConfig.getPlantBlockBiomassValue(event.block, newMeta) - PlantConfig.getPlantBlockBiomassValue(event.block, event.previousMetadata);
            bioSystem.onGrowth(biomassChange, true);
        }
    }

    @SubscribeEvent
    public void onBockPlaceEvent(BlockEvent.PlaceEvent event)
    {
        //Fix Forge (bug?) where onBlockAdded is called twice when a player places a block. This will also make sure
        //that if the block place event is cancelled no biomass will be added.
        //This basically just reverses one of the onBlockPlace events.
        Hooks.onPlantBlockRemoved(event.placedBlock, event.world, event.x, event.y, event.z);
    }


    @SubscribeEvent
    public void onBonemealUsedEvent(BonemealEvent event)
    {
        Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
        if (IBiotaAPI.API.addNutrientsToBioSystem(chunk, Settings.BONEMEAL_PHOSPHORUS, Settings.BONEMEAL_POTASSIUM, Settings.BONEMEAL_NITROGEN))
            event.setResult(Event.Result.ALLOW);
        else
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void allowFertilization(FertilizationEvent.Fertilize event)
    {
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public void onFertilization(FertilizationEvent.Fertilized event)
    {
        Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk);

        if (bioSystem != null)
        {
            int newMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
            float biomassChange = PlantConfig.getPlantBlockBiomassValue(event.block, newMeta) - PlantConfig.getPlantBlockBiomassValue(event.block, event.previousMetadata);
            bioSystem.onGrowth(biomassChange, true);
        }
    }
}
