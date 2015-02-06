package com.dynious.biota.config;

import com.dynious.biota.lib.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlantConfig
{
    public static final PlantConfig INSTANCE;

    private boolean initialized = false;

    private PlantConfigPart[] plants;

    static
    {
        //TODO: different biomass values for different metadata values
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(Launch.minecraftHome, "config" + File.separator + Reference.MOD_ID.toLowerCase() + File.separator + "plants.cfg");
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            INSTANCE = makeDefaultPlantConfig();
            String jsonString = gson.toJson(INSTANCE);
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
            PlantConfig plantConfig = null;
            try
            {
                String jsonString = FileUtils.readFileToString(file);
                plantConfig = gson.fromJson(jsonString, PlantConfig.class);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (plantConfig == null)
                plantConfig = makeDefaultPlantConfig();

            INSTANCE = plantConfig;
        }
    }

    private static PlantConfig makeDefaultPlantConfig()
    {
        List<PlantConfigPart> list = new ArrayList<PlantConfigPart>();
        list.add(new PlantConfigPart("net.minecraft.block.BlockGrass", 0.1F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockSapling", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockOldLog", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockOldLeaf", 1.0F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockTallGrass", 0.8F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockFlower", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMushroom", 0.3F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCrops", new float[] { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F }));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCactus", 0.6F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockPumpkin", 0.9F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockHugeMushroom", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMelon", 0.9F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockStem", new float[] { 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F }));
        list.add(new PlantConfigPart("net.minecraft.block.BlockVine", 0.2F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMycelium", 0.1F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockLilyPad", 0.2F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNetherWart", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCocoa", new float[] { 0.4F, 0.4F, 0.4F, 0.4F, 0.5F, 0.5F, 0.5F, 0.5F, 0.6F, 0.6F, 0.6F, 0.6F}));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNewLeaf", 1.0F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNewLog", 0.5F));
        list.add(new PlantConfigPart("net.minecraft.block.BlockDoublePlant", 0.6F));
        return new PlantConfig(list.toArray(new PlantConfigPart[list.size()]));
    }

    private PlantConfig(PlantConfigPart[] plantConfigParts)
    {
        this.plants = plantConfigParts;
    }

    public String[] getPlantClassNames()
    {
        String[] strings = new String[plants.length];
        for (int i = 0; i < plants.length; i++)
        {
            strings[i] = plants[i].plantClassName;
        }
        return strings;
    }

    public float getPlantBlockBiomassValue(Block block, int meta)
    {
        if (!initialized)
        {
            List<PlantConfigPart> plantsNotFound = new ArrayList<PlantConfigPart>();
            for (PlantConfigPart plantConfigPart : plants)
            {
                try
                {
                    plantConfigPart.clazz = Class.forName(plantConfigPart.plantClassName);
                } catch (ClassNotFoundException e)
                {
                    plantsNotFound.add(plantConfigPart);
                }
            }
            List<PlantConfigPart> list = Arrays.asList(plants);
            list.removeAll(plantsNotFound);
            plants = list.toArray(new PlantConfigPart[list.size()]);
            initialized = true;
        }
        for (PlantConfigPart plantConfigPart : plants)
        {
            if (plantConfigPart.clazz.isInstance(block))
            {
                if (meta >= 0 && plantConfigPart.plantBiomassValues != null && meta < plantConfigPart.plantBiomassValues.length)
                {
                    return plantConfigPart.plantBiomassValues[meta];
                }
                return plantConfigPart.plantBiomassValue;
            }
        }
        return 0;
    }

    private static class PlantConfigPart
    {
        private String plantClassName;
        private Float plantBiomassValue;
        private float[] plantBiomassValues;
        public Class clazz;

        private PlantConfigPart(String plantClassName, float plantBiomassValue)
        {
            this.plantClassName = plantClassName;
            this.plantBiomassValue = plantBiomassValue;
        }

        private PlantConfigPart(String plantClassName, float[] plantBiomassValues)
        {
            this.plantClassName = plantClassName;
            this.plantBiomassValues = plantBiomassValues;
        }
    }
}
