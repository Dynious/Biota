package com.dynious.biota;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.config.DeadPlantConfig;
import com.dynious.biota.lib.BlockAndMeta;
import net.minecraft.block.Block;

public class BiotaAPI implements IBiotaAPI
{
    public static final IBiotaAPI INSTANCE = new BiotaAPI();

    @Override
    public void registerDeadPlant(Block livingPlant, int livingMeta, Block deadPlant, int deadMeta)
    {
        DeadPlantConfig.setDeadPlant(new BlockAndMeta(livingPlant, livingMeta), new BlockAndMeta(deadPlant, deadMeta));
    }
}
