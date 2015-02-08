package com.dynious.biota.biosystem;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.procedure.TObjectFloatProcedure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.*;

public class BioSystemHandler
{
    private static boolean currentlyDecorating;
    private static Map<Chunk, BioSystem> bioSystemMap = new WeakHashMap<Chunk, BioSystem>();
    public static TObjectFloatMap<ChunkCoords> changeMap = new TObjectFloatHashMap<ChunkCoords>();
    public static List<BioSystem> stabalizeMap = new ArrayList<BioSystem>();

    public static void onChunkLoaded(Chunk chunk, NBTTagCompound compound)
    {
        assert chunk != null;

        BioSystem bioSystem;

        if (compound == null || compound.hasNoTags())
        {
            if (bioSystemMap.containsKey(chunk))
            {
                //We already have this chunk loaded, but apparently it changed, we'll need to recheck it, but keep the nutrients for minimal loss
                BioSystem bioSystem1 = bioSystemMap.remove(chunk);
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

        bioSystemMap.put(chunk, bioSystem);
    }

    public static void onChunkLoaded(Chunk chunk)
    {
        if (!bioSystemMap.containsKey(chunk))
            bioSystemMap.put(chunk, new BioSystem(chunk));
    }

    public static void onChunkUnload(Chunk chunk)
    {
        bioSystemMap.remove(chunk);
    }

    public static BioSystem getBioSystem(Chunk chunk)
    {
        return bioSystemMap.get(chunk);
    }

    public static Iterator<BioSystem> iterator()
    {
        return bioSystemMap.values().iterator();
    }

    public static void update()
    {
        //long time = System.nanoTime();
        changeMap.forEachEntry(ChunkCoordsProcedure.INSTANCE);
        changeMap.clear();

        if (!stabalizeMap.isEmpty())
        {
            List<BioSystem> copiedList = new ArrayList<BioSystem>(stabalizeMap);
            stabalizeMap.clear();
            for (BioSystem bioSystem : copiedList)
                bioSystem.setStableBacteriaValuesNearChunk();
        }

        Iterator<BioSystem> iterator = BioSystemHandler.iterator();
        while (iterator.hasNext())
        {
            iterator.next().update();
        }
        //System.out.println((float)(System.nanoTime() - time) / 1000000);
    }

    public static boolean isChunkAccessible()
    {
        return !currentlyDecorating;
    }

    public static void setDecoratingChunk(boolean isDecorating)
    {
        currentlyDecorating = isDecorating;
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

    private static class ChunkCoordsProcedure implements TObjectFloatProcedure<ChunkCoords>
    {

        public static final ChunkCoordsProcedure INSTANCE = new ChunkCoordsProcedure();

        private ChunkCoordsProcedure()
        {
        }

        @Override
        public boolean execute(ChunkCoords coords, float amount)
        {
            Chunk chunk = coords.world.getChunkFromChunkCoords(coords.x, coords.z);
            BioSystem bioSystem = getBioSystem(chunk);
            if (bioSystem != null)
            {
                bioSystem.addBiomass(amount);
            }
            else
            {
                System.out.println("DIDN'T FIND BIOSYSTEM!");
            }
            return true;
        }
    }
}
