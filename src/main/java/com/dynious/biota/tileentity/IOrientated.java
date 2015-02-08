package com.dynious.biota.tileentity;

import net.minecraftforge.common.util.ForgeDirection;

public interface IOrientated
{
    public ForgeDirection getOrientation();

    public void setOrientation(ForgeDirection orientation);
}
