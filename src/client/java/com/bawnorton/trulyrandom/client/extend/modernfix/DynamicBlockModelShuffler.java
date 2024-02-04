package com.bawnorton.trulyrandom.client.extend.modernfix;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface DynamicBlockModelShuffler extends ModelShuffler.BlockStates {
    default List<BlockState> trulyrandom$getBlockStates() {
        return Registries.BLOCK.stream()
                               .map(Block::getStateManager)
                               .map(StateManager::getStates)
                               .flatMap(Collection::stream)
                               .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    default void trulyrandom$shuffleModels(long seed) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        trulyrandom$resetModels();

        Random rnd = new Random(seed);
        for (List<BlockState> variant : buildPropertyMap().values()) {
            Collections.shuffle(variant, rnd);
            for (int i = 0; i < variant.size(); i++) {
                BlockState original = variant.get(i);
                BlockState randomised = variant.get((i + 1) % variant.size());
                trulyrandom$getRedirectMap().put(original, randomised);
            }
        }
    }
}
