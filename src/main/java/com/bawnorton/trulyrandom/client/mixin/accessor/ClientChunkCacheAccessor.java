package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@MixinEnvironment("client")
@Mixin(ClientChunkCache.class)
public interface ClientChunkCacheAccessor {
    @Accessor("storage")
    ClientChunkCache.Storage trulyrandom$storage();

    @MixinEnvironment("client")
    @Mixin(ClientChunkCache.Storage.class)
    interface StorageAccessor {
        @Accessor("chunks")
        AtomicReferenceArray<LevelChunk> trulyrandom$chunks();
    }
}
