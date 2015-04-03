package com.dynious.biota.asm;

import com.dynious.biota.lib.Reference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlantTransformerConfig
{
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static final PlantTransformerConfig INSTANCE;

    private PlantConfigPart[] plants;

    static
    {
        List<PlantTransformerConfig> loaders = new ArrayList<PlantTransformerConfig>();

        File file = new File(Launch.minecraftHome, "config" + File.separator + Reference.MOD_ID.toLowerCase());
        if (!file.exists())
        {
            file.mkdirs();
        }
        boolean foundVanilla = false;
        for (File foundFile : file.listFiles())
        {
            if (foundFile.getName().endsWith(".cfg") && foundFile.getName().startsWith("transformers"))
            {
                if (foundFile.getName().equals("transformersVanilla.cfg"))
                    foundVanilla = true;
                PlantTransformerConfig loader = readFile(file);
                if (loader != null)
                    loaders.add(loader);
            }
        }
        if (!foundVanilla)
        {
            PlantTransformerConfig loader = makeDefaultConfig();
            String jsonString = gson.toJson(loader);
            try
            {
                FileUtils.writeStringToFile(new File(file, "transformersVanilla.cfg"), jsonString);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        List<PlantConfigPart> parts = new ArrayList<PlantConfigPart>();
        for (PlantTransformerConfig loader : loaders)
        {
            parts.addAll(Arrays.asList(loader.plants));
        }
        INSTANCE = new PlantTransformerConfig(parts.toArray(new PlantConfigPart[parts.size()]));
    }

    private static PlantTransformerConfig readFile(File file)
    {
        try
        {
            String jsonString = FileUtils.readFileToString(file);
            return gson.fromJson(jsonString, PlantTransformerConfig.class);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static PlantTransformerConfig makeDefaultConfig()
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
