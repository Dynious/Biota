package com.dynious.biota.config;

import com.dynious.biota.Biota;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.lib.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BiomeConfig
{
    private static final float[] NORMAL_NUTRIENTS = { Settings.NORMAL_PHOSPHORUS , Settings.NORMAL_POTASSIUM , Settings.NORMAL_NITROGEN };
    private static BiomeInfo[] biomeInfoArray = new BiomeInfo[BiomeGenBase.getBiomeGenArray().length];

    public static void init()
    {
        Loader.load();
    }

    public static float[] getNutrientValuesForChunk(Chunk chunk)
    {
        int biomeID = getDominantBiomeInChunk(chunk);

        return getBiomeNutrientValues(biomeID);
    }

    public static float[] getRandomizedNutrientValuesForChunk(Chunk chunk)
    {
        Random random = new Random();
        float[] values = getNutrientValuesForChunk(chunk);
        values[0] = values[0] + (1F-(random.nextFloat()*2)) * Settings.DELTA_PHOSPHORUS * values[0];
        values[1] = values[1] + (1F-(random.nextFloat()*2)) * Settings.DELTA_POTASSIUM * values[1];
        values[2] = values[2] + (1F-(random.nextFloat()*2)) * Settings.DELTA_NITROGEN * values[2];

        return values;
    }

    public static float[] getBiomeNutrientValues(int biomeID)
    {
        if (biomeID < 0 || biomeID >= biomeInfoArray.length || biomeInfoArray[biomeID] == null)
            return NORMAL_NUTRIENTS.clone();

        return biomeInfoArray[biomeID].normalNutrients.clone();
    }

    public static void registerBiomeValue(int biomeID, float normalPhosphorus, float normalPotassium, float normalNitrogen)
    {
        if (biomeID >= 0 && biomeInfoArray.length > biomeID)
        {
            if (biomeInfoArray[biomeID] == null)
            {
                biomeInfoArray[biomeID] = new BiomeInfo(new float[] { normalPhosphorus, normalPotassium, normalNitrogen });
            }
            else
            {
                Biota.logger.warn("Biome with ID " + biomeID + " already registered! Skipping.");
            }
        }
        else
        {
            Biota.logger.warn("Invalid biome id: " + biomeID + "! Skipping.");
        }
    }

    private static int getDominantBiomeInChunk(Chunk chunk)
    {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (int i : chunk.getBiomeArray()) {
            Integer count = map.get(i);
            map.put(i, count != null ? count+1 : 0);
        }

        return Collections.max(map.entrySet(),
                new Comparator<Map.Entry<Integer, Integer>>()
                {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
                    {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                }).getKey();
    }

    private static class BiomeInfo
    {
        public float[] normalNutrients;

        public BiomeInfo(float[] normalNutrients)
        {
            this.normalNutrients = normalNutrients;
        }
    }

    private static class Loader
    {
        private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
        public LoaderPart[] biomes;

        private Loader(LoaderPart[] biomes)
        {
            this.biomes = biomes;
        }

        public static void load()
        {
            List<Loader> loaders = new ArrayList<Loader>();

            File file = new File(cpw.mods.fml.common.Loader.instance().getConfigDir(), Reference.MOD_ID.toLowerCase());
            if (!file.exists())
            {
                file.mkdirs();
            }
            boolean foundVanilla = false;
            for (File foundFile : file.listFiles())
            {
                if (foundFile.getName().endsWith(".cfg") && foundFile.getName().startsWith("biomes"))
                {
                    if (foundFile.getName().equals("biomesVanilla.cfg"))
                        foundVanilla = true;
                    Loader loader = readFile(file);
                    if (loader != null)
                        loaders.add(loader);
                }
            }
            if (!foundVanilla)
            {
                Loader loader = makeDefaultConfig();
                String jsonString = gson.toJson(loader);
                try
                {
                    FileUtils.writeStringToFile(new File(file, "biomesVanilla.cfg"), jsonString);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            for (Loader loader : loaders)
            {
                LoaderPart[] biomes1 = loader.biomes;
                for (LoaderPart biome : biomes1)
                {
                    if (biome.biomeID >= 0 && biomeInfoArray.length > biome.biomeID)
                    {
                        if (biomeInfoArray[biome.biomeID] == null)
                        {
                            biomeInfoArray[biome.biomeID] = new BiomeInfo(new float[]{biome.normalPhosphorus, biome.normalPotassium, biome.normalNitrogen});
                        }
                        else
                        {
                            Biota.logger.warn("Biome with ID " + biome.biomeID + " already registered! Skipping.");
                        }
                    }
                    else
                    {
                        Biota.logger.warn("Invalid biome id: " + biome.biomeID + "! Skipping.");
                    }
                }
            }
        }


    private static Loader readFile(File file)
    {
        try
        {
            String jsonString = FileUtils.readFileToString(file);
            return gson.fromJson(jsonString, Loader.class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

        private static Loader makeDefaultConfig()
        {
            List<LoaderPart> list = new ArrayList<LoaderPart>();
            list.add(new LoaderPart(0, 15F, 200F, 7.5F));
            list.add(new LoaderPart(1, 15F, 200F, 7.5F));
            list.add(new LoaderPart(2, 7.5F, 100F, 3.75F));
            list.add(new LoaderPart(3, 13F, 180F, 7F));
            list.add(new LoaderPart(4, 15F, 200F, 7.5F));
            list.add(new LoaderPart(5, 14F, 190F, 7.25F));
            list.add(new LoaderPart(6, 16F, 220F, 8F));
            list.add(new LoaderPart(7, 14F, 180F, 7.25F));
            list.add(new LoaderPart(8, 4F, 70F, 2F));
            list.add(new LoaderPart(9, 0F, 0F, 0F));
            list.add(new LoaderPart(10, 13F, 180F, 7F));
            list.add(new LoaderPart(11, 12F, 160F, 6F));
            list.add(new LoaderPart(12, 13F, 170F, 6.5F));
            list.add(new LoaderPart(13, 13F, 180F, 7F));
            list.add(new LoaderPart(14, 13F, 180F, 7F));
            list.add(new LoaderPart(15, 13F, 180F, 7F));
            list.add(new LoaderPart(16, 13F, 180F, 7F));
            list.add(new LoaderPart(17, 7.5F, 100F, 3.75F));
            list.add(new LoaderPart(18, 15F, 200, 7.5F));
            list.add(new LoaderPart(19, 13F, 180F, 7F));
            list.add(new LoaderPart(20, 14F, 190F, 7.25F));
            list.add(new LoaderPart(21, 17F, 280F, 9F));
            list.add(new LoaderPart(22, 17F, 280F, 9F));
            list.add(new LoaderPart(23, 17F, 280F, 9F));
            list.add(new LoaderPart(24, 15F, 200F, 7.5F));
            list.add(new LoaderPart(25, 8F, 120F, 4F));
            list.add(new LoaderPart(26, 13F, 180F, 7F));
            list.add(new LoaderPart(27, 15F, 200F, 7.5F));
            list.add(new LoaderPart(28, 15F, 200F, 7.5F));
            list.add(new LoaderPart(29, 15F, 200F, 7.5F));
            list.add(new LoaderPart(30, 13F, 180F, 7F));
            list.add(new LoaderPart(31, 13F, 180F, 7F));
            list.add(new LoaderPart(32, 14F, 190F, 7.25F));
            list.add(new LoaderPart(33, 13F, 180F, 7F));
            list.add(new LoaderPart(34, 14F, 190F, 7.25F));
            list.add(new LoaderPart(35, 11.5F, 155F, 5.75F));
            list.add(new LoaderPart(36, 11.5F, 155F, 5.75F));
            list.add(new LoaderPart(37, 7.5F, 100F, 3.75F));
            list.add(new LoaderPart(38, 7.5F, 100F, 3.75F));
            list.add(new LoaderPart(39, 7.5F, 100F, 3.75F));

            return new Loader(list.toArray(new LoaderPart[list.size()]));
        }

        private static class LoaderPart
        {
            public int biomeID;
            public float normalPhosphorus;
            public float normalPotassium;
            public float normalNitrogen;
            public LoaderPart(int biomeID, float normalPhosphorus, float normalPotassium, float normalNitrogen)
            {
                this.biomeID = biomeID;
                this.normalPhosphorus = normalPhosphorus;
                this.normalPotassium = normalPotassium;
                this.normalNitrogen = normalNitrogen;
            }
        }
    }
}
