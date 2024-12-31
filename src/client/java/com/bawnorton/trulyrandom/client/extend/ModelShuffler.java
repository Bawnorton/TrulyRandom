package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.client.mixin.accessor.StateAccessor;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
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
                for (Map.Entry<Property<?>, Comparable<?>> entry : state.getEntries().entrySet()) {
                    variant.append(StateAccessor.getPropertyMapPrinter().apply(entry));
                }
                variant.append(state.isOpaque());
                variant.append(state.getCullingShape());
                propertyMap.computeIfAbsent(variant.toString(), k -> new ArrayList<>()).add(state);
            }
            propertyMap.forEach((k, v) -> v.sort(Comparator.comparingInt(state -> Registries.BLOCK.getRawId(state.getBlock()))));
            return propertyMap;
        }
    }

    interface Items extends ModelShuffler<Identifier> {
    }
}
