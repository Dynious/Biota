package com.dynious.biota.api;

import net.minecraft.block.Block;

public interface IBiotaAPI
{
    public static final IBiotaAPI API = APIGetter.getAPI();

    public void registerDeadPlant(Block livingPlant, int livingMeta, Block deadPlant, int deadMeta);

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
