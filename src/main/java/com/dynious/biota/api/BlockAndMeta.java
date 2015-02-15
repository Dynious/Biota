package com.dynious.biota.api;

import net.minecraft.block.Block;

public class BlockAndMeta
{
    public Block block;
    public int meta;

    public BlockAndMeta(Block block, int meta)
    {
        this.block = block;
        this.meta = meta;
    }
}
