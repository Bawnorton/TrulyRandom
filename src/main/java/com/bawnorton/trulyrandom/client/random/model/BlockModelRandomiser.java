package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.ClientChunkCacheAccessor;
import com.bawnorton.trulyrandom.client.mixin.accessor.LevelRendererAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.concurrent.atomic.AtomicReferenceArray;

public class BlockModelRandomiser extends ModelRandomiser<BlockState> {
    @Override
    public void randomise(Minecraft minecraft, long seed) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) minecraft.getModelManager().getBlockStateModelSet();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.BLOCK_MODELS.invoker().onBlockModels(modelShuffler.trulyrandom$getRedirectMap());
        if(TrulyRandomClient.getRandomiser().getItemModelRandomiser().syncWithBlockModels(minecraft, false)) {
            ModelShuffler.Items itemModelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
            ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(itemModelShuffler.trulyrandom$getRedirectMap());
        }
    }

    @Override
    public void reset(Minecraft minecraft) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) minecraft.getModelManager().getBlockStateModelSet();
        modelShuffler.trulyrandom$resetModels();
        TrulyRandomClient.getRandomiser().getItemModelRandomiser().syncWithBlockModels(minecraft, true);
        reloadModels(minecraft);
    }

    @Override
    public void reloadModels(Minecraft minecraft) {
        if (minecraft.level == null) return;

        ClientChunkCache.Storage storage = ((ClientChunkCacheAccessor) minecraft.level.getChunkSource()).trulyrandom$storage();
        AtomicReferenceArray<LevelChunk> chunks = ((ClientChunkCacheAccessor.StorageAccessor) (Object) storage).trulyrandom$chunks();
        ChunkPos[] chunkPositions = new ChunkPos[chunks.length()];
        for (int i = 0; i < chunks.length(); i++) {
            LevelChunk chunk = chunks.get(i);
            if (chunk != null) chunkPositions[i] = chunk.getPos();
        }
        for (ChunkPos chunkPos : chunkPositions) {
            if (chunkPos != null) {
                for (int y = minecraft.level.getMinSectionY(); y < minecraft.level.getMaxSectionY(); y++) {
                    ((LevelRendererAccessor) minecraft.levelRenderer).trulyrandom$setSectionDirtyWithNeighbors(chunkPos.x(), y, chunkPos.z(), true);
                }
            }
        }
    }

    @Override
    public UnaryMap<BlockState> getRedirectMap(Minecraft minecraft) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) minecraft.getModelManager().getBlockStateModelSet();
        return modelShuffler.trulyrandom$getRedirectMap();
    }

    @Override
    public Module getModule() {
        return Module.BLOCK_MODELS;
    }

    public void updateBlockModels(UnaryMap<BlockState> redirectMap) {
        ModelShuffler.BlockStates modelShuffler = (ModelShuffler.BlockStates) Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        modelShuffler.trulyrandom$updateModels(redirectMap);
    }
}
