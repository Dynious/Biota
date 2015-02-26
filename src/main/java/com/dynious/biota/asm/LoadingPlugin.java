package com.dynious.biota.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"com.dynious.biota.asm", "com.google.gson"})
public class LoadingPlugin implements IFMLLoadingPlugin
{
    public LoadingPlugin()
    {
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[] { CoreTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}
