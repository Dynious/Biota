package com.dynious.biota.biosystem;

import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

public class BioSystem
{
    private static final Random random = new Random();

    /**
     * Phosphorus is used by plants for growth. Plants and animal waste will be turned into phosphates by bacteria when decomposed.
     * Phosphorus is usually the limiting factor in plant growth. It can be released slowly by weathering of rock but
     * can also be incorporated into rock. Bone meal has lots of phosphorus!
     */
    private byte phosphorus;

    /**
     * Potassium is essential for plants and will affect plants greatly when there's not enough available in the soil.
     * Plants will take up more than needed for healthy growth if there's enough available. Works a lot like phosphorus.
     * Bone meal has no potassium. Potassium can be gained by mining rock.
     */
    private byte potassium;

    /**
     * Nitrate is used by plants for growth. Plants and animal waste will be turned into ammonia by bacteria when decomposed.
     * This ammonia will be turned into nitrate by nitrifying bacteria. Nitrate can be removed from the ground and into
     * the atmosphere by denitrifying bacteria. Ammonia can be inserted in the ground from the atmosphere by nitrogen-fixing
     * soil bacteria.
     */
    private byte nitrogen;


    public BioSystem()
    {
        phosphorus = (byte) random.nextInt(256);
        potassium = (byte) random.nextInt(256);
        nitrogen = (byte) random.nextInt(256);
    }

    private BioSystem(byte phosphorus, byte potassium, byte nitrogen)
    {
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogen = nitrogen;
    }

    public static BioSystem loadFromNBT(NBTTagCompound compound)
    {
        byte phosphorus = compound.getByte("phosphorus");
        byte potassium = compound.getByte("potassium");
        byte nitrogen = compound.getByte("nitrogen");
        return new BioSystem(phosphorus, potassium, nitrogen);
    }

    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setByte("phosphorus", phosphorus);
        compound.setByte("potassium", potassium);
        compound.setByte("nitrogen", nitrogen);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(NEW_LINE);
        result.append(" phosphorus: ");
        result.append(phosphorus);
        result.append(NEW_LINE);
        result.append(" potassium: ");
        result.append(potassium);
        result.append(NEW_LINE);
        result.append(" nitrogen: ");
        result.append(nitrogen);
        result.append(NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
