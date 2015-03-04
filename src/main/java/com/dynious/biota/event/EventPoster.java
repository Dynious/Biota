package com.dynious.biota.event;

import com.dynious.biota.api.event.PlantEvent;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventPoster
{
    public static Event.Result postSpreadEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
    {
        Event event = new PlantEvent.SpreadEvent(block, world, x, y, z, lowestNutrientValue, lightValue);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    public static Event.Result postDeathEvent(Block block, World world, int x, int y, int z, float lowestNutrientValue, int lightValue)
    {
        Event event = new PlantEvent.DeathEvent(block, world, x, y, z, lowestNutrientValue, lightValue);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getResult();
    }
}
