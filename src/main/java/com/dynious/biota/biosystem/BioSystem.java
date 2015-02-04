package com.dynious.biota.biosystem;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.Chunk;

import java.util.Random;

public class BioSystem
{
    private static final Random RANDOM = new Random();

    private static final int TICKS_PER_UPDATE = 20;

    //168000 ticks per MC week. One week for 1.0 change in spread.
    private static final float SPREAD_RATE = TICKS_PER_UPDATE/168000;

    //24000 ticks per MC day. One day for 1.0 change.
    private static final float CHANGE_RATE = TICKS_PER_UPDATE/24000;

    public final Chunk chunk;
    private int tick = RANDOM.nextInt(20);

    /**
     * Stores the amount of plants in the chunk. Plant blocks can have different amounts 'plant value'.
     */
    private float biomass = -1F;

    /**
     * Phosphorus is used by plants for growth. Plants and animal waste will be turned into phosphates by bacteria when decomposed.
     * Phosphorus is usually the limiting factor in plant growth. It can be released slowly by weathering of rock but
     * can also be incorporated into rock. Bone meal has lots of phosphorus!
     *
     * 10 - 20 ppm normal
     * Normal Carbon:Phosphorus rate = 200:1 - 300:1
     */
    private float phosphorus;

    /**
     * Potassium is essential for plants and will affect plants greatly when there's not enough available in the soil.
     * Plants will take up more than needed for healthy growth if there's enough available. Works a lot like phosphorus.
     * Bone meal has no potassium. Potassium can be gained by mining rock.
     *
     * 150 - 250 ppm K normal.
     */
    private float potassium;

    /**
     * Nitrate is used by plants for growth. Plants and animal waste will be turned into ammonia by bacteria when decomposed.
     * This ammonia will be turned into nitrate by nitrifying bacteria. Nitrate can be removed from the ground and into
     * the atmosphere by denitrifying bacteria. Ammonia can be inserted in the ground from the atmosphere by nitrogen-fixing
     * soil bacteria.
     *
     * 5 - 10 ppm normal. 25+ ppm optimal
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
        this(chunk, 10 + RANDOM.nextFloat()*10, 150 + RANDOM.nextFloat()*100, 5 + RANDOM.nextFloat()*5);
    }

    private BioSystem(Chunk chunk, float phosphorus, float potassium, float nitrogen)
    {
        this(chunk, -1F, phosphorus, potassium, nitrogen, -1F, -1F);
        BioSystemInitThread.INSTANCE.addBioSystem(this);
    }

    private BioSystem(Chunk chunk, float biomass, float phosphorus, float potassium, float nitrogen, float decomposingBacteria, float nitrifyingBacteria)
    {
        this.chunk = chunk;
        this.biomass = biomass;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogen = nitrogen;
        this.decomposingBacteria = decomposingBacteria;
        this.nitrifyingBacteria = nitrifyingBacteria;
    }

    public void addBiomass(float amount)
    {
        if (biomass != -1F)
        {
            chunk.isModified = true;
            this.biomass += amount;
        }
    }

    public void setBiomass(float amount)
    {
        chunk.isModified = true;
        biomass = amount;
        setStableBacteriaValues();
    }

    public void setStableBacteriaValues()
    {
        decomposingBacteria = biomass + (RANDOM.nextFloat() - 0.5F)*10;
        nitrifyingBacteria = biomass + (RANDOM.nextFloat() - 0.5F)*10;
    }

    public void update()
    {
        tick++;

        if (tick % TICKS_PER_UPDATE == 0)
        {
            if (chunk.xPosition == -8 && chunk.zPosition == 18)
                System.out.println(this);

            chunk.isModified = true;
            //Spread BioSystem stuff to nearby chunks
            spreadToChunk(chunk.xPosition - 1, chunk.zPosition);
            spreadToChunk(chunk.xPosition + 1, chunk.zPosition);
            spreadToChunk(chunk.xPosition, chunk.zPosition - 1);
            spreadToChunk(chunk.xPosition, chunk.zPosition + 1);

            //BioSystem calculations
            //TODO: figure out good change rates, could be different for each variable
            phosphorus += Math.min(biomass, decomposingBacteria)*CHANGE_RATE;
            phosphorus -= biomass*CHANGE_RATE;

            potassium += Math.min(biomass, decomposingBacteria)*CHANGE_RATE;
            potassium -= biomass*CHANGE_RATE;

            nitrogen += Math.min(Math.min(biomass, decomposingBacteria), nitrifyingBacteria)*CHANGE_RATE;
            nitrogen -= biomass*CHANGE_RATE;

            decomposingBacteria += (biomass-decomposingBacteria)*CHANGE_RATE;
            nitrifyingBacteria += (Math.min(biomass, decomposingBacteria)-nitrifyingBacteria)*CHANGE_RATE;

            //TODO: high/low nutrient rates should affect plants
        }
    }

    private void spreadToChunk(int xPos, int yPos)
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

        float spreadP = SPREAD_RATE*dP;
        float spreadK = SPREAD_RATE*dK;
        float spreadN = SPREAD_RATE*dN;
        float spreadDB = SPREAD_RATE*dDB;
        float spreadNB = SPREAD_RATE*dNB;

        //if (chunk.xPosition == 0 && chunk.zPosition == 0)
        //    System.out.println("P = " + phosphorus + " dP = " + dP + " Spread = " + spreadP);

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
        float biomass = compound.getFloat("biomass");

        return new BioSystem(chunk, biomass, phosphorus, potassium, nitrogen, decomposingBacteria, nitrifyingBacteria);
    }

    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setFloat("phosphorus", phosphorus);
        compound.setFloat("potassium", potassium);
        compound.setFloat("nitrogen", nitrogen);
        compound.setFloat("decomposingBacteria", decomposingBacteria);
        compound.setFloat("nitrifyingBacteria", nitrifyingBacteria);
        compound.setFloat("biomass", biomass);
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(NEW_LINE);
        result.append(" biomass: ");
        result.append(biomass);
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
