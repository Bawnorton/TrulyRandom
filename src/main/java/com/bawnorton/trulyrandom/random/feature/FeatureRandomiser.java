package com.bawnorton.trulyrandom.random.feature;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;

public class FeatureRandomiser extends RandomiserModule {
    private final Object2ObjectMap<ChunkPos, RandomSource> randomCache = Object2ObjectMaps.synchronize(new Object2ObjectOpenHashMap<>());
    private final long seed;

    public FeatureRandomiser(long seed) {
        super();
        this.seed = seed;
    }

    public void clearChunk(ChunkPos pos) {
        randomCache.remove(pos);
    }

    public RandomSource getRandom(ChunkPos chunkPos) {
        return randomCache.computeIfAbsent(chunkPos, _ -> {
            long seed = this.seed ^ (chunkPos.pack() * 31L);
            return new SingleThreadedRandomSource(seed);
        });
    }

    @Override
    public Module getModule() {
        return Module.FEATURES;
    }
}
