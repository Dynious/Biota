package com.dynious.biota.biosystem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class BioSystem
{
    private static final Random random = new Random();

    private final Chunk chunk;
    private int tick = random.nextInt(20);

    /**
     * Phosphorus is used by plants for growth. Plants and animal waste will be turned into phosphates by bacteria when decomposed.
     * Phosphorus is usually the limiting factor in plant growth. It can be released slowly by weathering of rock but
     * can also be incorporated into rock. Bone meal has lots of phosphorus!
     */
    private float phosphorus;

    /**
     * Potassium is essential for plants and will affect plants greatly when there's not enough available in the soil.
     * Plants will take up more than needed for healthy growth if there's enough available. Works a lot like phosphorus.
     * Bone meal has no potassium. Potassium can be gained by mining rock.
     */
    private float potassium;

    /**
     * Nitrate is used by plants for growth. Plants and animal waste will be turned into ammonia by bacteria when decomposed.
     * This ammonia will be turned into nitrate by nitrifying bacteria. Nitrate can be removed from the ground and into
     * the atmosphere by denitrifying bacteria. Ammonia can be inserted in the ground from the atmosphere by nitrogen-fixing
     * soil bacteria.
     */
    private float nitrogen;

    /**
     * Bacteria that decompose dead stuff. When there are not enough of these bacteria, the nutrients in dead plants
     * and animals will not fully be reinserted into the biosystem. The amount needed depends on the amount of plants
     * and animals in the chunk.
     */
    private float decomposingBacteria;

    /**
     * Nitrifying bacteria are needed to convert the ammonia created by the Decomposing Bacteria to Nitrate used by
     * plants to grow. When there are not enough of these bacteria the amount of nitrogen will Nitrate in the soil
     * will slowly decline, causing growth issues.
     */
    private float nitrifyingBacteria;


    public BioSystem(Chunk chunk)
    {
        //TODO: should depend on the plants already present in the chunk, so we don't get huge amounts of dead chunks
        this(chunk, random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat());
    }

    private BioSystem(Chunk chunk, float phosphorus, float potassium, float nitrogen, float decomposingBacteria, float nitrifyingBacteria)
    {
        this.chunk = chunk;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogen = nitrogen;
        this.decomposingBacteria = decomposingBacteria;
        this.nitrifyingBacteria = nitrifyingBacteria;
    }

    public void update()
    {
        tick++;

        if (tick % 20 == 0)
        {
            spreadToNearbyChunks(chunk.xPosition - 1, chunk.zPosition);
            spreadToNearbyChunks(chunk.xPosition + 1, chunk.zPosition);
            spreadToNearbyChunks(chunk.xPosition, chunk.zPosition - 1);
            spreadToNearbyChunks(chunk.xPosition, chunk.zPosition + 1);
        }

    }

    private void spreadToNearbyChunks(int xPos, int yPos)
    {
        if (chunk.worldObj.chunkExists(xPos, yPos))
        {
            Chunk chunk1 = chunk.worldObj.getChunkFromChunkCoords(xPos, yPos);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk1);
            if (bioSystem != null)
                spread(bioSystem);
        }
    }

    private void spread(BioSystem bioSystem)
    {
        float dP = this.phosphorus - bioSystem.phosphorus;
        float dK = this.potassium - bioSystem.potassium;
        float dN = this.nitrogen - bioSystem.nitrogen;
        float dDB = this.decomposingBacteria - bioSystem.decomposingBacteria;
        float dNB = this.nitrifyingBacteria - bioSystem.nitrifyingBacteria;

        float spreadP = (float) (Math.ceil(dP)*6*Math.pow(dP/10, 2));
        float spreadK = (float) (Math.ceil(dK)*6*Math.pow(dK/10, 2));
        float spreadN = (float) (Math.ceil(dN)*6*Math.pow(dN/10, 2));
        float spreadDB = (float) (Math.ceil(dDB)*6*Math.pow(dDB/10, 2));
        float spreadNB = (float) (Math.ceil(dNB)*6*Math.pow(dNB/10, 2));

        if (chunk.xPosition == 0 && chunk.zPosition == 0)
            System.out.println("P = " + phosphorus + " dP = " + dP + " Spread = " + spreadP);

        this.phosphorus -= spreadP;
        bioSystem.phosphorus += spreadP;
        this.potassium -= spreadK;
        bioSystem.potassium += spreadK;
        this.nitrogen -= spreadN;
        bioSystem.nitrogen += spreadN;
        this.decomposingBacteria -= spreadDB;
        bioSystem.decomposingBacteria += spreadDB;
        this.nitrifyingBacteria -= spreadNB;
        bioSystem.nitrifyingBacteria += spreadNB;
    }


    public static BioSystem loadFromNBT(Chunk chunk, NBTTagCompound compound)
    {
        float phosphorus = compound.getFloat("phosphorus");
        float potassium = compound.getFloat("potassium");
        float nitrogen = compound.getFloat("nitrogen");
        float decomposingBacteria = compound.getFloat("decomposingBacteria");
        float nitrifyingBacteria = compound.getFloat("nitrifyingBacteria");
        return new BioSystem(chunk, phosphorus, potassium, nitrogen, decomposingBacteria, nitrifyingBacteria);
    }

    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setFloat("phosphorus", phosphorus);
        compound.setFloat("potassium", potassium);
        compound.setFloat("nitrogen", nitrogen);
        compound.setFloat("decomposingBacteria", decomposingBacteria);
        compound.setFloat("nitrifyingBacteria", nitrifyingBacteria);
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
        result.append(" decomposingBacteria: ");
        result.append(decomposingBacteria);
        result.append(NEW_LINE);
        result.append(" nitrifyingBacteria: ");
        result.append(nitrifyingBacteria);
        result.append(NEW_LINE);
        result.append("}");

        return result.toString();
    }
}
