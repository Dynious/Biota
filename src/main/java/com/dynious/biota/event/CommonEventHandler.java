package com.dynious.biota.event;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.asm.Hooks;
import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.BioSystemHandler;
import com.dynious.biota.config.PlantConfig;
import com.dynious.biota.helper.WorldHelper;
import com.dynious.biota.item.ModItems;
import com.dynious.biota.lib.MathLib;
import com.dynious.biota.lib.Settings;
import com.dynious.biota.network.NetworkHandler;
import com.dynious.biota.network.message.MessageBioSystemUpdate;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
        BioSystemHandler.onChunkLoaded(event.world, event.getChunk(), compound);
    }

    @SubscribeEvent
    public void onChuckLoad(ChunkEvent.Load event)
    {
        if (!event.getChunk().worldObj.isRemote)
        {
            BioSystemHandler.onChunkLoaded(event.world, event.getChunk());
        }
    }

    @SubscribeEvent
    public void onChuckDataSave(ChunkDataEvent.Save event)
    {
        //Chunk saved to disk
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.world, event.getChunk());
        if (bioSystem != null)
        {
            NBTTagCompound compound = new NBTTagCompound();
            bioSystem.saveToNBT(compound);
            event.getData().setTag("Biota", compound);
        }
        if (!event.getChunk().isChunkLoaded)
            BioSystemHandler.onChunkUnload(event.world, event.getChunk());
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            event.world.theProfiler.startSection("bioSystem");
            BioSystemHandler handler = BioSystemHandler.get(event.world);
            if (handler != null)
                handler.update();
            event.world.theProfiler.endSection();
        }
    }

    //TODO: Check if loading & unloading works so no memory leaks will happen
    @SubscribeEvent
    public void onPlayWatchChunk(ChunkWatchEvent.Watch event)
    {
        Chunk chunk = event.player.worldObj.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.player.worldObj, chunk);
        if (bioSystem != null)
        {
            NetworkHandler.INSTANCE.sendTo(new MessageBioSystemUpdate(bioSystem), event.player);
        }
    }

    @SubscribeEvent
    public void allowPlantGrowth(PlantGrowthEvent.AllowGrowthTick event)
    {
        Chunk chunk = event.world.getChunkFromBlockCoords(event.x, event.z);
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.world, chunk);

        if (bioSystem != null)
        {
            int meta = event.world.getBlockMetadata(event.x, event.y, event.z);
            float lowestNutrientPart = PlantConfig.getLowestNutrientPart(event.block, meta, bioSystem.getPhosphorus(), bioSystem.getPotassium(), bioSystem.getNitrogen());
            float nutrientGrowChance = MathLib.getFittedValue(lowestNutrientPart, Settings.NUTRIENT_AMOUNT_FOR_STOP_GROWTH, Settings.NUTRIENT_AMOUNT_FOR_NORMAL_GROWTH, Settings.NUTRIENT_AMOUNT_FOR_MAX_GROWTH);

            int lightValue = event.block.isOpaqueCube() ? WorldHelper.getLightValue(event.world, event.x, event.y + 1, event.z) : WorldHelper.getLightValue(event.world, event.x, event.y, event.z);
            float lightGrowChance = MathLib.getFittedValue(lightValue, Settings.LIGHT_VALUE_FOR_STOP_GROWTH, Settings.LIGHT_VALUE_FOR_NORMAL_GROWTH, Settings.LIGHT_VALUE_FOR_MAX_GROWTH);

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
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.world, chunk);

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
        if (IBiotaAPI.API.addNutrientsToBioSystem(event.world, chunk, Settings.BONEMEAL_PHOSPHORUS, Settings.BONEMEAL_POTASSIUM, Settings.BONEMEAL_NITROGEN))
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
        BioSystem bioSystem = BioSystemHandler.getBioSystem(event.world, chunk);

        if (bioSystem != null)
        {
            int newMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
            float biomassChange = PlantConfig.getPlantBlockBiomassValue(event.block, newMeta) - PlantConfig.getPlantBlockBiomassValue(event.block, event.previousMetadata);
            bioSystem.onGrowth(biomassChange, true);
        }
    }

    @SubscribeEvent
    public void onBlockDrops(BlockEvent.HarvestDropsEvent event)
    {
        if (event.block == Blocks.clay && !event.isSilkTouching)
        {
            for (int x = event.x - 1; x < event.x + 1; x++)
            {
                for (int y = event.y - 1; y < event.y + 1; y++)
                {
                    for (int z = event.z - 1; z < event.z + 1; z++)
                    {
                        if (event.world.getBlock(x, y, z).getMaterial() == Material.water)
                        {
                            int fortune = event.fortuneLevel;
                            if (fortune > 3)
                                fortune = 3;

                            if (event.world.rand.nextInt(10 - fortune * 3) == 0)
                                event.drops.add(new ItemStack(ModItems.potash));
                            return;
                        }
                    }
                }
            }
        }
    }
}
