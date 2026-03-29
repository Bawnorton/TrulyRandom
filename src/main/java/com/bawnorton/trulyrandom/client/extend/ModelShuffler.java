package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ModelShuffler<T> {
    void trulyrandom$shuffleModels(long seed);

    UnaryMap<T> trulyrandom$getRedirectMap();

    default void trulyrandom$updateModels(UnaryMap<T> redirectMap) {
        trulyrandom$resetModels();
        trulyrandom$getRedirectMap().putAll(redirectMap);
    }

    void trulyrandom$resetModels();

    boolean trulyrandom$isShuffled();

    interface BlockStates extends ModelShuffler<BlockState> {
        List<BlockState> trulyrandom$getBlockStates();

        default Map<String, List<BlockState>> buildPropertyMap() {
            Map<String, List<BlockState>> propertyMap = new HashMap<>();
            for (BlockState state : trulyrandom$getBlockStates()) {
                StringBuilder variant = new StringBuilder();
                for (Property<?> entry : state.getProperties()) {
                    variant.append(entry);
                }
                variant.append(state.canOcclude());
                variant.append(state.getOcclusionShape());
                propertyMap.computeIfAbsent(variant.toString(), _ -> new ArrayList<>()).add(state);
            }
            propertyMap.forEach((_, v) -> v.sort(Comparator.comparingInt(state -> BuiltInRegistries.BLOCK.getId(state.getBlock()))));
            return propertyMap;
        }
    }

    interface Items extends ModelShuffler<Identifier> {
    }
}
