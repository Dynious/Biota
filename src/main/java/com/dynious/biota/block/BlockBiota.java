package com.dynious.biota.block;

import com.dynious.biota.Biota;
import com.dynious.biota.api.IBiotaAPI;
import com.dynious.biota.lib.Reference;
import com.dynious.biota.tileentity.IOrientated;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBiota extends Block
{
    private boolean isPlant;

    public BlockBiota()
    {
        super(Material.rock);
    }

    public BlockBiota(Material material)
    {
        super(material);
        this.setCreativeTab(Biota.tabBiota);
    }

    public Block setAsPlant(float biomassValue)
    {
        return setAsPlant(new float[] { biomassValue });
    }

    public Block setAsPlant(float[] biomassValues)
    {
        this.isPlant = true;
        IBiotaAPI.API.registerPlantValue(this, biomassValues);
        return this;
    }

    public boolean isPlant()
    {
        return isPlant;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess world, int x, int y, int z)
    {
        int color = super.colorMultiplier(world, x, y, z);
        if (isPlant)
            color = IBiotaAPI.API.getPlantColorMultiplier(color, x, z);
        return color;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        if (isPlant)
            IBiotaAPI.API.onPantBlockAdded(this, world, x, y, z);
        super.onBlockAdded(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        if (isPlant)
            IBiotaAPI.API.onPantBlockRemoved(this, world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public String getUnlocalizedName()
    {
        return String.format("tile.%s%s", Reference.RESOURCE_PREFIX, getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        blockIcon = iconRegister.registerIcon(String.format("%s", getUnwrappedUnlocalizedName(this.getUnlocalizedName())));
    }

    protected String getUnwrappedUnlocalizedName(String unlocalizedName)
    {
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLiving, ItemStack itemStack)
    {
        if (world.getTileEntity(x, y, z) instanceof IOrientated)
        {
            ForgeDirection direction = ForgeDirection.NORTH;
            int facing = MathHelper.floor_double(entityLiving.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

            if (facing == 0)
            {
                direction = ForgeDirection.NORTH;
            }
            else if (facing == 1)
            {
                direction = ForgeDirection.EAST;
            }
            else if (facing == 2)
            {
                direction = ForgeDirection.SOUTH;
            }
            else if (facing == 3)
            {
                direction = ForgeDirection.WEST;
            }

            ((IOrientated) world.getTileEntity(x, y, z)).setOrientation(direction);
        }
    }
}
