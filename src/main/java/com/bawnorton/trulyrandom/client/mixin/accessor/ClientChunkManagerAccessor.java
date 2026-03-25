package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.atomic.AtomicReferenceArray;

@Mixin(ClientChunkManager.class)
public interface ClientChunkManagerAccessor {
    @Accessor
    ClientChunkManager.ClientChunkMap getChunks();

    @Mixin(ClientChunkManager.ClientChunkMap.class)
    interface ClientChunkMapAccessor {
        @Accessor
        AtomicReferenceArray<WorldChunk> getChunks();
    }
}
