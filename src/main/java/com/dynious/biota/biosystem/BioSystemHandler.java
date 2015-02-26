package com.dynious.biota.biosystem;

import com.dynious.biota.Biota;
import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.procedure.TObjectFloatProcedure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class BioSystemHandler
{
    private static WeakHashMap<World, BioSystemHandler> handlers = new WeakHashMap<World, BioSystemHandler>();

    private Map<Chunk, BioSystem> bioSystemMap = new WeakHashMap<Chunk, BioSystem>();
    public TObjectFloatMap<ChunkCoords> biomassChangeMap = new TObjectFloatHashMap<ChunkCoords>();
    public TObjectFloatMap<ChunkCoords> nitrogenFixationChangeMap = new TObjectFloatHashMap<ChunkCoords>();
    public List<BioSystem> stabilizeList = new ArrayList<BioSystem>();

    public static void onChunkLoaded(World world, Chunk chunk, NBTTagCompound compound)
    {
        assert chunk != null;

        BioSystemHandler handler;
        if (!handlers.containsKey(world))
        {
            handler = new BioSystemHandler();
            handlers.put(world, handler);
        }
        else
            handler = handlers.get(world);

        BioSystem bioSystem;

        if (compound == null || compound.hasNoTags())
        {
            if (handler.bioSystemMap.containsKey(chunk))
            {
                //We already have this chunk loaded, but apparently it changed, we'll need to recheck it, but keep the nutrients for minimal loss
                BioSystem bioSystem1 = handler.bioSystemMap.remove(chunk);
                bioSystem = new BioSystem(chunk, bioSystem1.getPhosphorus(), bioSystem1.getPotassium(), bioSystem1.getNitrogen());
                BioSystemInitThread.addBioSystem(bioSystem);
            }
            else
            {
                //Existing chunk, but new BioSystem!
                bioSystem = new BioSystem(chunk);
                BioSystemInitThread.addBioSystem(bioSystem);
            }
        }
        else
        {
            bioSystem = BioSystem.loadFromNBT(chunk, compound);
        }

        handler.bioSystemMap.put(chunk, bioSystem);
    }

    public static void onChunkLoaded(World world, Chunk chunk)
    {
        BioSystemHandler handler;
        if (!handlers.containsKey(world))
        {
            handler = new BioSystemHandler();
            handlers.put(world, handler);
        }
        else
            handler = handlers.get(world);

        if (!handler.bioSystemMap.containsKey(chunk))
            handler.bioSystemMap.put(chunk, new BioSystem(chunk));
    }

    public static void onChunkUnload(World world, Chunk chunk)
    {
        BioSystemHandler handler = handlers.get(world);
        if (handler != null)
            handler.bioSystemMap.remove(chunk);
    }

    public static BioSystemHandler get(World world)
    {
        return handlers.get(world);
    }

    public static BioSystem getBioSystem(World world, Chunk chunk)
    {
        BioSystemHandler handler = handlers.get(world);
        if (handler != null)
            return handler.getBioSystem(chunk);
        return null;
    }

    public BioSystem getBioSystem(Chunk chunk)
    {
        return bioSystemMap.get(chunk);
    }

    public Iterator<BioSystem> iterator()
    {
        return bioSystemMap.values().iterator();
    }

    public void update()
    {
        biomassChangeMap.forEachEntry(BiomassProcedure.INSTANCE);
        biomassChangeMap.clear();

        nitrogenFixationChangeMap.forEachEntry(NitrogenFixationProcedure.INSTANCE);
        nitrogenFixationChangeMap.clear();

        if (!stabilizeList.isEmpty())
        {
            List<BioSystem> copiedList = new ArrayList<BioSystem>(stabilizeList);
            stabilizeList.clear();
            for (BioSystem bioSystem : copiedList)
                bioSystem.setStableBacteriaValuesNearChunk();
        }

        Iterator<BioSystem> iterator = iterator();
        while (iterator.hasNext())
        {
            iterator.next().update();
        }
    }

    public static class ChunkCoords
    {
        public World world;
        public int x, z;

        public ChunkCoords(World world, int x, int z)
        {
            this.world = world;
            this.x = x;
            this.z = z;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ChunkCoords that = (ChunkCoords) o;

            if (x != that.x) return false;
            if (z != that.z) return false;
            if (!world.equals(that.world)) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result = world.hashCode();
            result = 31 * result + x;
            result = 31 * result + z;
            return result;
        }
    }

    private static class BiomassProcedure implements TObjectFloatProcedure<ChunkCoords>
    {
        public static final BiomassProcedure INSTANCE = new BiomassProcedure();

        private BiomassProcedure()
        {
        }

        @Override
        public boolean execute(ChunkCoords coords, float amount)
        {
            Chunk chunk = coords.world.getChunkFromChunkCoords(coords.x, coords.z);
            BioSystem bioSystem = getBioSystem(coords.world, chunk);
            if (bioSystem != null)
            {
                bioSystem.addBiomass(amount);
            }
            else
            {
                Biota.logger.warn(String.format("Couldn't find BioSystem at: %d %d", coords.x, coords.z));
            }
            return true;
        }
    }

    private static class NitrogenFixationProcedure implements TObjectFloatProcedure<ChunkCoords>
    {
        public static final NitrogenFixationProcedure INSTANCE = new NitrogenFixationProcedure();

        private NitrogenFixationProcedure()
        {
        }

        @Override
        public boolean execute(ChunkCoords coords, float amount)
        {
            Chunk chunk = coords.world.getChunkFromChunkCoords(coords.x, coords.z);
            BioSystem bioSystem = getBioSystem(coords.world, chunk);
            if (bioSystem != null)
            {
                bioSystem.addNitrogenFixation(amount);
            }
            else
            {
                Biota.logger.warn(String.format("Couldn't find BioSystem at: %d %d", coords.x, coords.z));
            }
            return true;
        }
    }
}
