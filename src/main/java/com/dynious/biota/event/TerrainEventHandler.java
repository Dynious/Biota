package com.dynious.biota.event;

import com.dynious.biota.block.ModBlocks;
import com.dynious.biota.lib.Settings;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

public class TerrainEventHandler
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBiomeDecoration(DecorateBiomeEvent.Decorate event)
    {
        if (event.type == DecorateBiomeEvent.Decorate.EventType.FLOWERS && (event.getResult() == Event.Result.ALLOW || event.getResult() == Event.Result.DEFAULT))
        {
            for (int i = 0; i < Settings.FLOWER_QUANTITY; i++)
            {
                int x = event.chunkX + event.rand.nextInt(16) + 8;
                int z = event.chunkZ + event.rand.nextInt(16) + 8;
                int y = event.world.getTopSolidOrLiquidBlock(x, z);
                Block randomPlant = ModBlocks.plants[event.rand.nextInt(ModBlocks.plants.length)];

                if (event.world.isAirBlock(x, y, z) && (!event.world.provider.hasNoSky || y < 255) && randomPlant.canBlockStay(event.world, x, y, z))
                {
                    event.world.setBlock(x, y, z, randomPlant, 0, 2);
                }
            }
        }
    }
}
