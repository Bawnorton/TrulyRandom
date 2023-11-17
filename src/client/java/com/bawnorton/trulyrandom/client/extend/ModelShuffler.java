package com.bawnorton.trulyrandom.client.extend;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;

import java.util.Map;

public interface ModelShuffler<T> {
    void trulyrandom$shuffleModels(long seed);

    Map<T, T> trulyrandom$getOriginalRandomisedMap();

    void trulyrandom$resetModels();

    boolean trulyRandom$isShuffled();

    interface BlockStates extends ModelShuffler<BlockState> {
    }

    interface Items extends ModelShuffler<Item> {
    }
}
