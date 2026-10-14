package com.bawnorton.trulyrandom.random.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureRandomiser extends RandomiserModule {
    private final long seed;

    public StructureRandomiser(long seed) {
        super();
        this.seed = seed;
    }

    public Holder<Structure> getReplacement(Registry<Structure> structures, Holder<Structure> source, ResourceKey<Level> level, ChunkPos chunkPos) {
        long randomSeed = seed
                ^ (chunkPos.pack() * 31L)
                ^ (level.identifier().hashCode() * 127L)
                ^ (source.unwrapKey().orElseThrow().identifier().hashCode() * 8191L);
        RandomSource random = new SingleThreadedRandomSource(randomSeed);
        return structures.getRandom(random).orElseThrow();
    }

    @Override
    public Module getModule() {
        return Module.STRUCTURES;
    }
}
