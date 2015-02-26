package com.dynious.biota.config;

import com.dynious.biota.Biota;
import com.dynious.biota.api.BlockAndMeta;
import com.dynious.biota.api.DefaultPlantSpreader;
import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.api.IPlantSpreader;
import com.dynious.biota.biosystem.spreader.TallGrassSpreader;
import com.dynious.biota.block.ModBlocks;
import com.dynious.biota.lib.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlantConfig
{
    private static Map<Block, PlantInfo> plantInfoMap = new HashMap<Block, PlantInfo>();
    private static List<LinkedBlockAndMeta> livingDeadList = new ArrayList<LinkedBlockAndMeta>();

    public static void init()
    {
        Loader.load();
        IBiotaAPI.API.registerDeadPlant(ModBlocks.grass, -1, Blocks.dirt, -1);
        IBiotaAPI.API.registerDeadPlant(Blocks.tallgrass, -1, Blocks.deadbush, -1);

        IBiotaAPI.API.registerPlantSpreader(Blocks.tallgrass, new TallGrassSpreader());
    }

    public static float getPlantBlockBiomassValue(Block block, int meta)
    {
        PlantInfo plantInfo = plantInfoMap.get(block);

        if (plantInfo == null)
            return 0F;

        if (meta >= 0 && meta < plantInfo.values.length)
        {
            return plantInfo.values[meta];
        }
        return plantInfo.values[0];
    }

    public static void registerPlantValue(Block plant, float[] biomassValues)
    {
        if (biomassValues != null && biomassValues.length > 0)
            plantInfoMap.put(plant, new PlantInfo(biomassValues));
    }

    public static void registerPlantSpreader(Block plant, IPlantSpreader spreader)
    {
        PlantInfo plantInfo = plantInfoMap.get(plant);
        if (plantInfo != null)
            plantInfo.spreader = spreader;
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

    public static IPlantSpreader getPlantSpreader(Block block)
    {
        PlantInfo plantInfo = plantInfoMap.get(block);
        return plantInfo.spreader;
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

    private static class PlantInfo
    {
        private final float[] values;
        public IPlantSpreader spreader;

        public PlantInfo(float[] values)
        {
            this.values = values;
        }

        public PlantInfo(float[] values, IPlantSpreader spreader)
        {
            this.values = values;
            this.spreader = spreader;
        }
    }

    private static class Loader
    {
        public LoaderPart[] plants;

        private Loader(LoaderPart[] plants)
        {
            this.plants = plants;
        }

        public static void load()
        {
            Loader loader = null;

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            File file = new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), Reference.MOD_ID.toLowerCase() + File.separator + "plants.cfg");
            if (!file.exists())
            {
                file.getParentFile().mkdirs();
                loader = makeDefaultPlantConfig();
                String jsonString = gson.toJson(loader);
                try
                {
                    FileUtils.writeStringToFile(file, jsonString);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    String jsonString = FileUtils.readFileToString(file);
                    loader = gson.fromJson(jsonString, Loader.class);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                if (loader == null)
                    loader = makeDefaultPlantConfig();

            }
            LoaderPart[] plants1 = loader.plants;
            for (int i = 0; i < plants1.length; i++)
            {
                LoaderPart part = plants1[i];
                if (part.blockName != null)
                {
                    int index = part.blockName.indexOf(':');
                    Block block = GameRegistry.findBlock(part.blockName.substring(0, index), part.blockName.substring(index + 1));
                    if (block != null)
                    {
                        float[] biomassValues;
                        if (part.plantBiomassValues != null)
                            biomassValues = part.plantBiomassValues;
                        else
                            biomassValues = new float[]{part.plantBiomassValue};

                        if (part.useDefaultSpreader != null && part.useDefaultSpreader)
                            plantInfoMap.put(block, new PlantInfo(biomassValues, DefaultPlantSpreader.INSTANCE));
                        else
                            plantInfoMap.put(block, new PlantInfo(biomassValues));
                    } else
                    {
                        Biota.logger.warn("Unable to find plant block:" + part.blockName);
                    }
                }
                else
                {
                    Biota.logger.warn("Unable to find plant block at index: " + i);
                }
            }
        }

        private static Loader makeDefaultPlantConfig()
        {
            List<LoaderPart> list = new ArrayList<LoaderPart>();
            list.add(new LoaderPart("minecraft:grass", 0.1F, false));
            list.add(new LoaderPart("minecraft:sapling", 0.5F, false));
            list.add(new LoaderPart("minecraft:log", 0.5F, false));
            list.add(new LoaderPart("minecraft:log2", 0.5F, false));
            list.add(new LoaderPart("minecraft:leaves", 1.0F, false));
            list.add(new LoaderPart("minecraft:leaves2", 1.0F, false));
            list.add(new LoaderPart("minecraft:tallgrass", 0.8F, false));
            list.add(new LoaderPart("minecraft:yellow_flower", 0.5F, true));
            list.add(new LoaderPart("minecraft:red_flower", 0.5F, true));
            list.add(new LoaderPart("minecraft:brown_mushroom", 0.3F, true));
            list.add(new LoaderPart("minecraft:red_mushroom", 0.3F, true));
            list.add(new LoaderPart("minecraft:mossy_cobblestone", 0.1F, false));
            list.add(new LoaderPart("minecraft:wheat", new float[] { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F }, true));
            list.add(new LoaderPart("minecraft:cactus", 0.6F, true));
            list.add(new LoaderPart("minecraft:reeds", 0.4F, true));
            list.add(new LoaderPart("minecraft:pumpkin", 0.9F, false));
            list.add(new LoaderPart("minecraft:brown_mushroom_block", 0.7F, false));
            list.add(new LoaderPart("minecraft:red_mushroom_block", 0.7F, false));
            list.add(new LoaderPart("minecraft:melon_block", 0.9F, false));
            list.add(new LoaderPart("minecraft:pumpkin_stem", new float[] { 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F }, true));
            list.add(new LoaderPart("minecraft:melon_stem", new float[] { 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F }, true));
            list.add(new LoaderPart("minecraft:vine", 0.2F, true));
            list.add(new LoaderPart("minecraft:mycelium", 0.1F, false));
            list.add(new LoaderPart("minecraft:waterlily", 0.2F, true));
            list.add(new LoaderPart("minecraft:nether_wart", 0.5F, false));
            list.add(new LoaderPart("minecraft:cocoa", new float[] { 0.4F, 0.4F, 0.4F, 0.4F, 0.5F, 0.5F, 0.5F, 0.5F, 0.6F, 0.6F, 0.6F, 0.6F}, true));
            list.add(new LoaderPart("minecraft:carrots", new float[] { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F }, true));
            list.add(new LoaderPart("minecraft:potatoes", new float[] { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F }, true));
            list.add(new LoaderPart("minecraft:double_plant", 0.6F, true));
            return new Loader(list.toArray(new LoaderPart[list.size()]));
        }

        private static class LoaderPart
        {
            public String blockName;
            public Float plantBiomassValue;
            public float[] plantBiomassValues;
            public Boolean useDefaultSpreader;

            public LoaderPart(String blockName, float plantBiomassValue, boolean useDefaultSpreader)
            {
                this.blockName = blockName;
                this.plantBiomassValue = plantBiomassValue;
                this.useDefaultSpreader = !useDefaultSpreader ? null : true;
            }

            public LoaderPart(String blockName, float[] plantBiomassValues, boolean useDefaultSpreader)
            {
                this.blockName = blockName;
                this.plantBiomassValues = plantBiomassValues;
                this.useDefaultSpreader = !useDefaultSpreader ? null : true;
            }
        }
    }
}
