package com.dynious.biota.api;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IBiotaAPI
{
    public static final IBiotaAPI API = APIGetter.getAPI();

    public void registerDeadPlant(Block livingPlant, int livingMeta, Block deadPlant, int deadMeta);

    public void registerPlantValue(Block plantBlock, float biomassValue);

    public void registerPlantValue(Block plantBlock, float[] biomassValues);

    public void onPantBlockAdded(Block plantBlock, World world, int x, int y, int z);

    public void onPantBlockRemoved(Block plantBlock, World world, int x, int y, int z);

    public void onPantTick(Block plantBlock, World world, int x, int y, int z);

    @SideOnly(Side.CLIENT)
    public int getPlantColorMultiplier(int originalColor, int x, int y);

    static class APIGetter
    {
        public static IBiotaAPI getAPI()
        {
            try
            {
                Class<?> clazz = Class.forName("com.dynious.biota.BiotaAPI");
                return (IBiotaAPI) clazz.getField("INSTANCE").get(null);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }
}
