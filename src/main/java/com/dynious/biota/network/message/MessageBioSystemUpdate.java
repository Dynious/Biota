package com.dynious.biota.network.message;

import com.dynious.biota.biosystem.BioSystem;
import com.dynious.biota.biosystem.ClientBioSystem;
import com.dynious.biota.biosystem.ClientBioSystemHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class MessageBioSystemUpdate implements IMessage, IMessageHandler<MessageBioSystemUpdate, IMessage>
{
    int dimensionId, x, z;
    float phosphorus, potassium, nitrogen;

    public MessageBioSystemUpdate()
    {
    }

    public MessageBioSystemUpdate(BioSystem bioSystem)
    {
        Chunk chunk = bioSystem.chunkReference.get();
        if (chunk != null)
        {
            dimensionId = chunk.worldObj.provider.dimensionId;
            x = chunk.xPosition;
            z = chunk.zPosition;
            phosphorus = bioSystem.getPhosphorus();
            potassium = bioSystem.getPotassium();
            nitrogen = bioSystem.getNitrogen();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        dimensionId = buf.readInt();
        x = buf.readInt();
        z = buf.readInt();
        phosphorus = buf.readFloat();
        potassium = buf.readFloat();
        nitrogen = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(dimensionId);
        buf.writeInt(x);
        buf.writeInt(z);
        buf.writeFloat(phosphorus);
        buf.writeFloat(potassium);
        buf.writeFloat(nitrogen);
    }

    @Override
    public IMessage onMessage(MessageBioSystemUpdate message, MessageContext ctx)
    {
        //World world = DimensionManager.getWorld(message.dimensionId);
        Chunk chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(message.x, message.z);
        ClientBioSystem bioSystem = new ClientBioSystem(message.phosphorus, message.potassium, message.nitrogen);
        ClientBioSystemHandler.bioSystemMap.put(chunk, bioSystem);

        //Re-render whole chunk to update colors
        //System.out.println(String.format("RE-RENDER AT: %d %d", message.x, message.z));
        Minecraft.getMinecraft().theWorld.markBlockRangeForRenderUpdate((message.x << 4) + 1, 0, (message.z << 4) + 1, (message.x << 4) + 14, 256, (message.z << 4) + 14);

        return null;
    }
}
