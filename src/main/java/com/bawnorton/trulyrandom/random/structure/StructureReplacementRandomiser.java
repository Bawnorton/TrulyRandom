package com.bawnorton.trulyrandom.random.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Map;

public class StructureReplacementRandomiser extends RandomiserModule {
    private final Map<Key, UnaryMap<Structure>> structureReplacmements = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private final Map<Key, RandomSource> randomCache = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private final long seed;

    public StructureReplacementRandomiser(long seed) {
        super();
        this.seed = seed;
    }

    public void  registerReplacement(ResourceKey<Level> level, ChunkPos pos, Structure replaced, Structure with) {
        structureReplacmements.computeIfAbsent(new Key(level, pos), _ -> UnaryMap.of()).put(replaced, with);
    }

    public Structure getReplacementFor(ResourceKey<Level> level, ChunkPos pos, Structure structure) {
        UnaryMap<Structure> replacementMap = structureReplacmements.get(new Key(level, pos));
        if (replacementMap == null || replacementMap.isEmpty()) return structure;

        return replacementMap.getOrDefault(structure, structure);
    }

    public void clearChunk(ResourceKey<Level> level, ChunkPos pos) {
        Key key = new Key(level, pos);
        structureReplacmements.remove(key);
        randomCache.remove(key);
    }

    public RandomSource getRandom(ResourceKey<Level> level, ChunkPos chunkPos) {
        return randomCache.computeIfAbsent(new Key(level, chunkPos), _ -> {
            long seed = this.seed ^ (chunkPos.pack() * 31L) ^ (level.hashCode() * 127L);
            return new SingleThreadedRandomSource(seed);
        });
    }

    @Override
    public Module getModule() {
        return Module.STRUCTURES;
    }

    private record Key(ResourceKey<Level> level, ChunkPos chunkPos) {}
}
