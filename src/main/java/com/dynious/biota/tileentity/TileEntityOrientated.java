package com.dynious.biota.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityOrientated extends TileEntityBiota implements IOrientated
{
    private ForgeDirection orientation;

    public TileEntityOrientated()
    {
        this.orientation = ForgeDirection.NORTH;
    }

    @Override
    public ForgeDirection getOrientation()
    {
        return orientation;
    }

    @Override
    public void setOrientation(ForgeDirection orientation)
    {
        this.orientation = orientation;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setByte("orientation", (byte) orientation.ordinal());
    }
}
