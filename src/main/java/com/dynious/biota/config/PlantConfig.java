package com.dynious.biota.config;

import com.dynious.biota.Biota;
import com.dynious.biota.api.BlockAndMeta;
import com.dynious.biota.api.DefaultPlantSpreader;
import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.api.IPlantSpreader;
import com.dynious.biota.biosystem.spreader.TallGrassSpreader;
import com.dynious.biota.block.ModBlocks;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.lib.Settings;
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
    private static final float[][] NORMAL_NUTRIENTS = { { Settings.NORMAL_PHOSPHORUS }, { Settings.NORMAL_POTASSIUM }, { Settings.NORMAL_NITROGEN } };
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
            plantInfoMap.put(plant, new PlantInfo(biomassValues, NORMAL_NUTRIENTS));
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
        return plantInfo == null ? null : plantInfo.spreader;
    }

    public static float getLowestNutrientPart(Block block, int meta, float currentPhosphorus, float currentPotassium, float currentNitrogen)
    {
        PlantInfo plantInfo = plantInfoMap.get(block);

        if (plantInfo == null)
            return 1F;

        return Math.min(currentPhosphorus / getNutrientValue(plantInfo, 0, meta), Math.min(currentPotassium / getNutrientValue(plantInfo, 1, meta), currentNitrogen / getNutrientValue(plantInfo, 2, meta)));
    }

    private static float getNutrientValue(PlantInfo plantInfo, int type, int meta)
    {
        float[] values = plantInfo.normalNutrients[type];

        if (meta >= 0 && meta < values.length)
        {
            return values[meta];
        }
        return values[0];
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
        public final float[][] normalNutrients;

        public PlantInfo(float[] values, float[][] normalNutrients)
        {
            this.values = values;
            this.normalNutrients = normalNutrients;
        }

        public PlantInfo(float[] values, IPlantSpreader spreader, float[][] normalNutrients)
        {
            this.values = values;
            this.spreader = spreader;
            this.normalNutrients = normalNutrients;
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
                            biomassValues = new float[]{ 0F };

                        float[][] normalNutrients = NORMAL_NUTRIENTS;
                        if (part.normalPhosphorus != null)
                            normalNutrients[0] = part.normalPhosphorus;
                        if (part.normalPotassium != null)
                            normalNutrients[1] = part.normalPotassium;
                        if (part.normalNitrogen != null)
                            normalNutrients[2] = part.normalNitrogen;

                        if (part.useDefaultSpreader != null && part.useDefaultSpreader)
                            plantInfoMap.put(block, new PlantInfo(biomassValues, DefaultPlantSpreader.INSTANCE, normalNutrients));
                        else
                            plantInfoMap.put(block, new PlantInfo(biomassValues, normalNutrients));
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
            list.add(new LoaderPart("minecraft:grass", false, 0.1F));
            list.add(new LoaderPart("minecraft:sapling", false, 0.5F));
            list.add(new LoaderPart("minecraft:log", false, 0.5F));
            list.add(new LoaderPart("minecraft:log2", false, 0.5F));
            list.add(new LoaderPart("minecraft:leaves", false, 1.0F));
            list.add(new LoaderPart("minecraft:leaves2", false, 1.0F));
            list.add(new LoaderPart("minecraft:tallgrass", false, 0.8F));
            list.add(new LoaderPart("minecraft:yellow_flower", true, 0.5F));
            list.add(new LoaderPart("minecraft:red_flower", true, 0.5F));
            list.add(new LoaderPart("minecraft:brown_mushroom", true, 0.3F));
            list.add(new LoaderPart("minecraft:red_mushroom", true, 0.3F));
            list.add(new LoaderPart("minecraft:mossy_cobblestone", false, 0.1F));
            list.add(new LoaderPart("minecraft:wheat", true, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F));
            list.add(new LoaderPart("minecraft:cactus", true, 0.6F));
            list.add(new LoaderPart("minecraft:reeds", true, 0.4F));
            list.add(new LoaderPart("minecraft:pumpkin", false, 0.9F));
            list.add(new LoaderPart("minecraft:brown_mushroom_block", false, 0.7F));
            list.add(new LoaderPart("minecraft:red_mushroom_block", false, 0.7F));
            list.add(new LoaderPart("minecraft:melon_block", false, 0.9F));
            list.add(new LoaderPart("minecraft:pumpkin_stem", true, 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F));
            list.add(new LoaderPart("minecraft:melon_stem", true, 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F));
            list.add(new LoaderPart("minecraft:vine", true, 0.2F));
            list.add(new LoaderPart("minecraft:mycelium", false, 0.1F));
            list.add(new LoaderPart("minecraft:waterlily", true, 0.2F));
            list.add(new LoaderPart("minecraft:nether_wart", false, 0.5F));
            list.add(new LoaderPart("minecraft:cocoa", true, 0.4F, 0.4F, 0.4F, 0.4F, 0.5F, 0.5F, 0.5F, 0.5F, 0.6F, 0.6F, 0.6F, 0.6F));
            list.add(new LoaderPart("minecraft:carrots", true, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F));
            list.add(new LoaderPart("minecraft:potatoes", true, 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F));
            list.add(new LoaderPart("minecraft:double_plant", true, 0.6F));
            return new Loader(list.toArray(new LoaderPart[list.size()]));
        }

        private static class LoaderPart
        {
            public String blockName;
            public float[] plantBiomassValues;
            public float[] normalPhosphorus;
            public float[] normalPotassium;
            public float[] normalNitrogen;
            public Boolean useDefaultSpreader;

            public LoaderPart(String blockName, boolean useDefaultSpreader, float... plantBiomassValues)
            {
                this.blockName = blockName;
                this.plantBiomassValues = plantBiomassValues;
                this.useDefaultSpreader = !useDefaultSpreader ? null : true;
            }

            public LoaderPart(String blockName, float[] normalPhosphorus, float[] normalPotassium, float[] normalNitrogen, boolean useDefaultSpreader, float... plantBiomassValues)
            {
                this.blockName = blockName;
                this.plantBiomassValues = plantBiomassValues;
                this.useDefaultSpreader = !useDefaultSpreader ? null : true;
                this.normalPhosphorus = normalPhosphorus;
                this.normalPotassium = normalPotassium;
                this.normalNitrogen = normalNitrogen;
            }
        }
    }
}
