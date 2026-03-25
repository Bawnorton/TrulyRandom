package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.ClientChunkManagerAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.WorldRendererInvoker;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class BlockModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(MinecraftClient client, long seed) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) client.getBlockRenderManager().getModels();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.BLOCK_MODELS.invoker().onBlockModels(modelShuffler.trulyrandom$getRedirectMap());
    }

    @Override
    public void reset(MinecraftClient client) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) client.getBlockRenderManager().getModels();
        modelShuffler.trulyrandom$resetModels();
        reloadModels(client);
    }

    @Override
    public void reloadModels(MinecraftClient client) {
        if (client.world == null) return;

        ClientChunkManager.ClientChunkMap chunkMap = ((ClientChunkManagerAccessor) client.world.getChunkManager()).getChunks();
        AtomicReferenceArray<WorldChunk> chunks = ((ClientChunkManagerAccessor.ClientChunkMapAccessor) (Object) chunkMap).getChunks();
        ChunkPos[] chunkPositions = new ChunkPos[chunks.length()];
        for (int i = 0; i < chunks.length(); i++) {
            WorldChunk chunk = chunks.get(i);
            if (chunk != null) chunkPositions[i] = chunk.getPos();
        }
        for (ChunkPos chunkPos : chunkPositions) {
            if (chunkPos != null) {
                for (int y = client.world.getBottomSectionCoord(); y < client.world.getTopSectionCoord(); y++) {
                    ((WorldRendererInvoker) client.worldRenderer).invokeScheduleChunkRender(chunkPos.x, y, chunkPos.z, true);
                }
            }
        }
    }

    @Override
    public Module getModule() {
        return Module.BLOCK_MODELS;
    }

    public void updateBlockModels(UnaryMap<BlockState> redirectMap) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) MinecraftClient.getInstance().getBlockRenderManager().getModels();
        modelShuffler.trulyrandom$updateModels(redirectMap);
    }
}
