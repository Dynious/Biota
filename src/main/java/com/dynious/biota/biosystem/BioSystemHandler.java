package com.dynious.biota.biosystem;

import gnu.trove.map.TObjectFloatMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.procedure.TObjectFloatProcedure;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Iterator;
import java.util.Map;

public class BioSystemHandler
{
    private static Map<Chunk, BioSystem> bioSystemMap = new THashMap<Chunk, BioSystem>();
    public static TObjectFloatMap<ChunkCoords> changeMap = new TObjectFloatHashMap<ChunkCoords>();

    public static void onChunkLoaded(Chunk chunk, NBTTagCompound compound)
    {
        assert chunk != null;

        BioSystem bioSystem;

        if (compound == null || compound.hasNoTags())
        {
            if (bioSystemMap.containsKey(chunk))
                return;

            bioSystem = new BioSystem(chunk);
        }
        else
        {
            bioSystem = BioSystem.loadFromNBT(chunk, compound);
        }

        bioSystemMap.put(chunk, bioSystem);
        if (chunk.xPosition == 0 && chunk.zPosition == 0)
            System.out.println(bioSystem.toString());
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
        changeMap.forEachEntry(ChunkCoordsProcedure.INSTANCE);
        changeMap.clear();
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
                System.out.println("AMOUNT: " + amount);
                bioSystem.addBiomass(amount);
            }
            else
            {
                System.out.println("DIDN'T FIND BIOSYSTEM!");
            }
            return false;
        }
    }
}
