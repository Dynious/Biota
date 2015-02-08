package com.dynious.biota.config;

import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.asm.PlantConfigLoader;
import com.dynious.biota.lib.BlockAndMeta;
import gnu.trove.map.hash.THashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlantConfig
{
    private static Map<Class<? extends Block>, float[]> plantValues;
    private static List<LinkedBlockAndMeta> livingDeadList = new ArrayList<LinkedBlockAndMeta>();

    static
    {
        plantValues = new THashMap<Class<? extends Block>, float[]>();
        for (PlantConfigLoader.PlantConfigPart plantConfigPart : PlantConfigLoader.INSTANCE.getPlantConfig())
        {
            try
            {
                Class<? extends Block> plantClass = (Class<? extends Block>) Class.forName(plantConfigPart.plantClassName);
                float[] biomassValues;
                if (plantConfigPart.plantBiomassValues != null)
                    biomassValues = plantConfigPart.plantBiomassValues;
                else
                    biomassValues = new float[] { plantConfigPart.plantBiomassValue };
                plantValues.put(plantClass, biomassValues);

            } catch (ClassNotFoundException e)
            {
            }
        }
    }

    public static void init()
    {
        IBiotaAPI.API.registerDeadPlant(Blocks.grass, -1, Blocks.dirt, -1);
        IBiotaAPI.API.registerDeadPlant(Blocks.tallgrass, -1, Blocks.deadbush, -1);
    }

    public static float getPlantBlockBiomassValue(Block block, int meta)
    {
        float[] values = plantValues.get(block.getClass());

        if (values == null)
            return 0F;

        if (meta >= 0 && meta < values.length)
        {
            return values[meta];
        }
        return values[0];
    }

    public static void registerPlantValue(Class<? extends Block> plant, float[] biomassValues)
    {
        if (!plantValues.containsKey(plant) && biomassValues != null && biomassValues.length > 0)
            plantValues.put(plant, biomassValues);
    }

    public static void registerDeadPlant(BlockAndMeta livingPlant, BlockAndMeta deadPlant)
    {
        livingDeadList.add(new LinkedBlockAndMeta(livingPlant, deadPlant));
    }

    public static BlockAndMeta getDeadPlant(Block livingPlant, int livingMeta)
    {
        for (LinkedBlockAndMeta linkedBlockAndMeta : livingDeadList)
        {
            if (linkedBlockAndMeta.key.block == livingPlant)
            {
                if (linkedBlockAndMeta.key.meta == livingMeta || linkedBlockAndMeta.key.meta == -1)
                    return linkedBlockAndMeta.value;
            }
        }
        return null;
    }

    private static class LinkedBlockAndMeta
    {
        private BlockAndMeta key;
        private BlockAndMeta value;

        public LinkedBlockAndMeta(BlockAndMeta key, BlockAndMeta value)
        {
            this.key = key;
            this.value = value;
        }
    }
}
