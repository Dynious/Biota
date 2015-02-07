package com.dynious.biota.network;

import com.dynious.biota.lib.Reference;
import com.dynious.biota.network.message.MessageBioSystemUpdate;
import cpw.mods.fml.relauncher.Side;

public class NetworkHandler
{
    public static final SimpleNetworkWrapperExtension INSTANCE = new SimpleNetworkWrapperExtension(Reference.MOD_ID.toLowerCase());

    public static void init()
    {
        INSTANCE.registerMessage(MessageBioSystemUpdate.class, MessageBioSystemUpdate.class, 0, Side.CLIENT);
    }
}
