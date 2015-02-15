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

public class PlantTransformerConfig
{
    public static final PlantTransformerConfig INSTANCE;

    private PlantConfigPart[] plants;

    static
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File(Launch.minecraftHome, "config" + File.separator + Reference.MOD_ID.toLowerCase() + File.separator + "plantTransformer.cfg");
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
            PlantTransformerConfig plantTransformerConfig = null;
            try
            {
                String jsonString = FileUtils.readFileToString(file);
                plantTransformerConfig = gson.fromJson(jsonString, PlantTransformerConfig.class);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            if (plantTransformerConfig == null)
                plantTransformerConfig = makeDefaultPlantConfig();

            INSTANCE = plantTransformerConfig;
        }
    }

    private static PlantTransformerConfig makeDefaultPlantConfig()
    {
        List<PlantConfigPart> list = new ArrayList<PlantConfigPart>();
        list.add(new PlantConfigPart("net.minecraft.block.BlockGrass", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockSapling", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockLog", false));
        list.add(new PlantConfigPart("net.minecraft.block.BlockOldLeaf", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNewLeaf", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockTallGrass", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockFlower", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMushroom", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCrops",  true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCactus",  true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockReed",  true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockPumpkin",  true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockHugeMushroom",  true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMelon", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockStem", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockVine", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockMycelium", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockLilyPad", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockNetherWart", false));
        list.add(new PlantConfigPart("net.minecraft.block.BlockCocoa", true));
        list.add(new PlantConfigPart("net.minecraft.block.BlockDoublePlant", true));
        return new PlantTransformerConfig(list.toArray(new PlantConfigPart[list.size()]));
    }

    private PlantTransformerConfig(PlantConfigPart[] plantConfigParts)
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

    public static class PlantConfigPart
    {
        public final String plantClassName;
        public final boolean shouldChangeColor;

        private PlantConfigPart(String plantClassName, boolean shouldChangeColor)
        {
            this.plantClassName = plantClassName;
            this.shouldChangeColor = shouldChangeColor;
        }
    }
}
