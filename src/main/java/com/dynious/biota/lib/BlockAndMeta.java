package com.dynious.biota.lib;

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
