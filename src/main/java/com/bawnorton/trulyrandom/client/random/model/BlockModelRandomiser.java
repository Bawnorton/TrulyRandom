package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.ClientChunkManagerAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.WorldRendererInvoker;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class BlockModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(Minecraft minecraft, long seed) {
        /*ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) minecraft.getBlockEntityRenderDispatcher().getModels();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.BLOCK_MODELS.invoker().onBlockModels(modelShuffler.trulyrandom$getRedirectMap());*/
    }

    @Override
    public void reset(Minecraft minecraft) {
        /*ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) minecraft.getBlockRenderManager().getModels();
        modelShuffler.trulyrandom$resetModels();
        reloadModels(minecraft);*/
    }

    @Override
    public void reloadModels(Minecraft minecraft) {
        /*if (minecraft.level == null) return;

        ClientChunkManager.ClientChunkMap chunkMap = ((ClientChunkManagerAccessor) minecraft.level.getChunkSource()).getChunks();
        AtomicReferenceArray<WorldChunk> chunks = ((ClientChunkManagerAccessor.ClientChunkMapAccessor) (Object) chunkMap).getChunks();
        ChunkPos[] chunkPositions = new ChunkPos[chunks.length()];
        for (int i = 0; i < chunks.length(); i++) {
            WorldChunk chunk = chunks.get(i);
            if (chunk != null) chunkPositions[i] = chunk.getPos();
        }
        for (ChunkPos chunkPos : chunkPositions) {
            if (chunkPos != null) {
                for (int y = minecraft.level.getBottomSectionCoord(); y < minecraft.level.getTopSectionCoord(); y++) {
                    ((WorldRendererInvoker) client.levelRenderer).invokeScheduleChunkRender(chunkPos.x, y, chunkPos.z, true);
                }
            }
        }*/
    }

    @Override
    public Module getModule() {
        return Module.BLOCK_MODELS;
    }

    public void updateBlockModels(UnaryMap<BlockState> redirectMap) {
        /*ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) Minecraft.getInstance().getBlockRenderManager().getModels();
        modelShuffler.trulyrandom$updateModels(redirectMap);*/
    }
}
