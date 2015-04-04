package com.dynious.biota.biosystem;

import com.dynious.biota.Biota;
import com.dynious.biota.config.BiomeConfig;
import com.dynious.biota.lib.Settings;
import com.dynious.biota.network.NetworkHandler;
import com.dynious.biota.network.message.MessageBioSystemUpdate;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import java.lang.ref.WeakReference;
import java.util.Random;

public class BioSystem
{
    private static final Random RANDOM = new Random();

    public final WeakReference<Chunk> chunkReference;
    private int tick = RANDOM.nextInt(Settings.TICKS_PER_BIOSYSTEM_UPDATE);

    /**
     * Stores the amount of plants in the chunk. Plant blocks can have different amounts 'plant value'.
     */
    private float biomass;

    /**
     * Stores the amount of nitrogen fixated by the plants in this chunk.
     */
    private float nitrogenFixation;

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

    /*
    Soil density: 1360 kg/m^3
    Around 768 dirt blocks per chunk (calculate this? biome dependant?) (16*16*3)
    1044480 kg of dirt per chunk
    We assume all parts in soil weigh the same so: 1.04448 kg per millionth part (for ppm)
    1 ppm ~= 1 kg
     */


    public BioSystem(Chunk chunk)
    {
        this(chunk, BiomeConfig.getRandomizedNutrientValuesForChunk(chunk));
    }

    private BioSystem(Chunk chunk ,float[] nutrients)
    {
        this(chunk, nutrients[0], nutrients[1], nutrients[2]);
    }

    public BioSystem(Chunk chunk, float phosphorus, float potassium, float nitrogen)
    {
        this(chunk, 0F, 0F, phosphorus, potassium, nitrogen, 0F, 0F);
    }

    private BioSystem(Chunk chunk, float biomass, float nitrogenFixation, float phosphorus, float potassium, float nitrogen, float decomposingBacteria, float nitrifyingBacteria)
    {
        this.chunkReference = new WeakReference<Chunk>(chunk);
        this.biomass = biomass;
        this.nitrogenFixation = nitrogenFixation;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogen = nitrogen;
        this.decomposingBacteria = decomposingBacteria;
        this.nitrifyingBacteria = nitrifyingBacteria;
    }

    public void addBiomass(float amount)
    {
        setChunkModified();
        this.biomass += amount;
    }

    public void onGrowth(float bioMassIncrease, boolean addBiomass)
    {
        if (addBiomass)
            addBiomass(bioMassIncrease);
        phosphorus -= bioMassIncrease*Settings.BIOMASS_PHOSPHORUS_RATE;
        potassium -= bioMassIncrease*Settings.BIOMASS_POTASSIUM_RATE;
        nitrogen -= bioMassIncrease*Settings.BIOMASS_NITROGEN_RATE;
        setChunkModified();
    }

    public void setBiomass(float amount)
    {
        setChunkModified();
        biomass = amount;
        setStableBacteriaValues();
    }

    public float getBiomass()
    {
        return biomass;
    }

    public float getNitrogenFixation()
    {
        return nitrogenFixation;
    }

    public void addNitrogenFixation(float amount)
    {
        setChunkModified();
        this.nitrogenFixation += amount;
    }

    public float getPhosphorus()
    {
        return phosphorus;
    }

    public float getPotassium()
    {
        return potassium;
    }

    public float getNitrogen()
    {
        return nitrogen;
    }

    public float getDecomposingBacteria()
    {
        return decomposingBacteria;
    }

    public float getNitrifyingBacteria()
    {
        return nitrifyingBacteria;
    }

    public void setNitrogenFixation(float nitrogenFixation)
    {
        this.nitrogenFixation = nitrogenFixation;
    }

    public void setPhosphorus(float phosphorus)
    {
        this.phosphorus = phosphorus;
    }

    public void setPotassium(float potassium)
    {
        this.potassium = potassium;
    }

    public void setNitrogen(float nitrogen)
    {
        this.nitrogen = nitrogen;
    }

    public void setStableBacteriaValues()
    {
        decomposingBacteria = biomass + RANDOM.nextFloat()*(Settings.BACTERIA_GROWTH_MAX*biomass - biomass);
        nitrifyingBacteria = biomass + RANDOM.nextFloat()*(Settings.BACTERIA_GROWTH_MAX*biomass - biomass);
    }

    public void setStableBacteriaValuesNearChunk()
    {
        setStableBacteriaValues();
        Chunk chunk = chunkReference.get();
        if (chunk != null)
        {
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition + 1, chunk.zPosition + 1);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition, chunk.zPosition + 1);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition + 1, chunk.zPosition);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition - 1, chunk.zPosition);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition, chunk.zPosition - 1);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition - 1, chunk.zPosition - 1);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition - 1, chunk.zPosition + 1);
            getAndStabilizeChunk(chunk.worldObj, chunk.xPosition + 1, chunk.zPosition - 1);
        }
    }

    public void getAndStabilizeChunk(World world, int x, int z)
    {
        if (world.chunkExists(x, z))
        {
            BioSystem bioSystem = BioSystemHandler.getBioSystem(world, world.getChunkFromChunkCoords(x, z));
            if (bioSystem != null)
                bioSystem.setStableBacteriaValues();
        }

    }

    public void update()
    {
        tick++;

        if (tick % Settings.TICKS_PER_BIOSYSTEM_UPDATE == 0)
        {
            setChunkModified();
            Chunk chunk = chunkReference.get();

            if (chunk != null)
            {
                if (chunk.xPosition == 0 && chunk.zPosition == 0)
                {
                    System.out.println(String.format("PRE: Biomass: %f, Nitrogen Fixation: %f, Phosphorus: %f, Potassium: %f, Nitrogen %f, Decomposing Bacteria: %f, Nirtifying Bacteria %f", this.getBiomass(), this.getNitrogenFixation(), this.getPhosphorus(), this.getPotassium(), this.getNitrogen(), this.getDecomposingBacteria(), this.getNitrifyingBacteria()));
                }
                //TODO: BALANCE! BIOMASS INCREASE HAS A VERY DRAMATIC EFFECT, NUTRIENT USAGE TOO HIGH.
                //Bacteria calculations
                float biomassBacteriaRate = biomass / decomposingBacteria;
                if (biomassBacteriaRate > Settings.BACTERIA_GROWTH_MAX)
                {
                    decomposingBacteria += decomposingBacteria * Settings.BACTERIA_CHANGE_RATE;
                }
                else if (biomassBacteriaRate < Settings.BACTERIA_DEATH)
                {
                    decomposingBacteria -= (1-biomassBacteriaRate)*decomposingBacteria * Settings.BACTERIA_CHANGE_RATE;
                }

                float nirtifyingBacteriaRate = (Math.min(biomass, decomposingBacteria) + nitrogenFixation) / nitrifyingBacteria;
                if (nirtifyingBacteriaRate > Settings.BACTERIA_GROWTH_MAX)
                {
                    nitrifyingBacteria += nitrifyingBacteria * Settings.BACTERIA_CHANGE_RATE;
                }
                else if (nirtifyingBacteriaRate < Settings.BACTERIA_DEATH)
                {
                    nitrifyingBacteria -= (1-nirtifyingBacteriaRate)*nitrifyingBacteria * Settings.BACTERIA_CHANGE_RATE;
                }

                //Nutrient calculations
                //TODO: figure out good change rates
                phosphorus += Math.min(biomass, decomposingBacteria) * Settings.PHOSPHORUS_CHANGE_RATE;
                phosphorus -= biomass * Settings.PHOSPHORUS_CHANGE_RATE;
                phosphorus = Math.max(0, phosphorus);

                potassium += Math.min(biomass, decomposingBacteria) * Settings.POTASSIUM_CHANGE_RATE;
                potassium -= biomass * Settings.POTASSIUM_CHANGE_RATE;
                potassium = Math.max(0, potassium);

                //TODO: nitrogen fixation should be calculated diffently (should not be dependant on nitrogen change rate in plants)
                nitrogen += Math.min(Math.min(biomass, decomposingBacteria) + nitrogenFixation, nitrifyingBacteria) * Settings.NITROGEN_CHANGE_RATE;
                nitrogen -= biomass * Settings.NITROGEN_CHANGE_RATE;
                nitrogen = Math.max(0, nitrogen);

                //Spread BioSystem stuff to nearby chunks
                spreadToChunk(chunk, chunk.xPosition - 1, chunk.zPosition);
                spreadToChunk(chunk, chunk.xPosition + 1, chunk.zPosition);
                spreadToChunk(chunk, chunk.xPosition, chunk.zPosition - 1);
                spreadToChunk(chunk, chunk.xPosition, chunk.zPosition + 1);

                if (chunk.xPosition == 0 && chunk.zPosition == 0)
                {
                    System.out.println(String.format("SPREAD: Biomass: %f, Nitrogen Fixation: %f, Phosphorus: %f, Potassium: %f, Nitrogen %f, Decomposing Bacteria: %f, Nirtifying Bacteria %f", this.getBiomass(), this.getNitrogenFixation(), this.getPhosphorus(), this.getPotassium(), this.getNitrogen(), this.getDecomposingBacteria(), this.getNitrifyingBacteria()));
                }

                //Send the chunk biomass changes to all clients watching this chunk
                NetworkHandler.INSTANCE.sendToPlayersWatchingChunk(new MessageBioSystemUpdate(this), (WorldServer) chunk.worldObj, chunk.xPosition, chunk.zPosition);
            }
        }
    }

    private void spreadToChunk(Chunk chunk, int xPos, int zPos)
    {
        if (chunk.worldObj.chunkExists(xPos, zPos))
        {
            Chunk chunk1 = chunk.worldObj.getChunkFromChunkCoords(xPos, zPos);
            BioSystem bioSystem = BioSystemHandler.getBioSystem(chunk.worldObj, chunk1);
            if (bioSystem != null)
                spread(bioSystem);
            else
                Biota.logger.warn(String.format("Couldn't find BioSystem at: %d %d", xPos, zPos));
        }
    }

    private void spread(BioSystem bioSystem)
    {
        float dP = this.phosphorus - bioSystem.phosphorus;
        float dK = this.potassium - bioSystem.potassium;
        float dN = this.nitrogen - bioSystem.nitrogen;
        float dDB = this.decomposingBacteria - bioSystem.decomposingBacteria;
        float dNB = this.nitrifyingBacteria - bioSystem.nitrifyingBacteria;

        float spreadP = Settings.BIOSYSTEM_SPREAD_RATE *dP;
        float spreadK = Settings.BIOSYSTEM_SPREAD_RATE *dK;
        float spreadN = Settings.BIOSYSTEM_SPREAD_RATE *dN;
        float spreadDB = Settings.BIOSYSTEM_SPREAD_RATE *dDB;
        float spreadNB = Settings.BIOSYSTEM_SPREAD_RATE *dNB;

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

    public void setChunkModified()
    {
        Chunk chunk1 = chunkReference.get();
        if (chunk1 != null)
        {
            chunk1.isModified = true;
        }
    }

    public static BioSystem loadFromNBT(Chunk chunk, NBTTagCompound compound)
    {
        float biomass = compound.getFloat("biomass");
        float nitrogenFixation = compound.getFloat("nitrogenFixation");
        float phosphorus = compound.getFloat("phosphorus");
        float potassium = compound.getFloat("potassium");
        float nitrogen = compound.getFloat("nitrogen");
        float decomposingBacteria = compound.getFloat("decomposingBacteria");
        float nitrifyingBacteria = compound.getFloat("nitrifyingBacteria");

        return new BioSystem(chunk, biomass, nitrogenFixation, phosphorus, potassium, nitrogen, decomposingBacteria, nitrifyingBacteria);
    }

    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setFloat("biomass", biomass);
        compound.setFloat("nitrogenFixation", nitrogenFixation);
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
        result.append(" biomass: ");
        result.append(biomass);
        result.append(NEW_LINE);
        result.append(" nitrogen fixation: ");
        result.append(nitrogenFixation);
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
