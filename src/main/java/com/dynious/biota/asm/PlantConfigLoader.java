package com.dynious.biota.asm;

import com.dynious.biota.lib.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlantConfigLoader
{
    public static final PlantConfigLoader INSTANCE;

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
            PlantConfigLoader plantConfigLoader = null;
            try
            {
                String jsonString = FileUtils.readFileToString(file);
                plantConfigLoader = gson.fromJson(jsonString, PlantConfigLoader.class);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (plantConfigLoader == null)
                plantConfigLoader = makeDefaultPlantConfig();

            INSTANCE = plantConfigLoader;
        }
    }

    private static PlantConfigLoader makeDefaultPlantConfig()
    {
        List<PlantConfigPart> list = new ArrayList<PlantConfigPart>();
        list.add(new PlantConfigPart("net.minecraft.block.BlockGrass", 0.1F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockSapling", 0.5F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockLog", 0.5F, false));
        list.add(new PlantConfigPart("net.minecraft.block.BlockOldLeaf", 1.0F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNewLeaf", 1.0F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockTallGrass", 0.8F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockFlower", 0.5F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMushroom", 0.3F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCrops", new float[] { 0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F, 0.7F, 0.8F }, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCactus", 0.6F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockPumpkin", 0.9F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockHugeMushroom", 0.5F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMelon", 0.9F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockStem", new float[] { 0.1F, 0.125F, 0.15F, 0.175F, 0.2F, 0.225F, 0.250F, 0.275F }, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockVine", 0.2F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMycelium", 0.1F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockLilyPad", 0.2F, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNetherWart", 0.5F, false));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCocoa", new float[] { 0.4F, 0.4F, 0.4F, 0.4F, 0.5F, 0.5F, 0.5F, 0.5F, 0.6F, 0.6F, 0.6F, 0.6F}, true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockDoublePlant", 0.6F, true));
        return new PlantConfigLoader(list.toArray(new PlantConfigPart[list.size()]));
    }

    private PlantConfigLoader(PlantConfigPart[] plantConfigParts)
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

    public boolean shouldPlantChangeColor(String name)
    {
        for (PlantConfigPart part : plants)
        {
            if (name.equals(part.plantClassName))
                return part.shouldChangeColor;
        }
        return true;
    }

    public PlantConfigPart[] getPlantConfig()
    {
        return plants;
    }

    public static class PlantConfigPart
    {
        public final String plantClassName;
        public Float plantBiomassValue;
        public float[] plantBiomassValues;
        public final boolean shouldChangeColor;

        private PlantConfigPart(String plantClassName, float plantBiomassValue, boolean shouldChangeColor)
        {
            this.plantClassName = plantClassName;
            this.plantBiomassValue = plantBiomassValue;
            this.shouldChangeColor = shouldChangeColor;
        }

        private PlantConfigPart(String plantClassName, float[] plantBiomassValues, boolean shouldChangeColor)
        {
            this.plantClassName = plantClassName;
            this.plantBiomassValues = plantBiomassValues;
            this.shouldChangeColor = shouldChangeColor;
        }
    }
}
