package com.bawnorton.trulyrandom.client.extend;

import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

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
        boolean trulyrandom$forceStatesToUseSameModel();

        boolean trulyrandom$ignoreModelOcclusion();

        boolean trulyrandom$ignoreStateProperties();

        List<BlockState> trulyrandom$getBlockStates();

        default Map<String, List<BlockState>> buildPropertyMap() {
            Map<String, List<BlockState>> propertyMap = new HashMap<>();
            for (BlockState state : trulyrandom$getBlockStates()) {
                StringBuilder variant = new StringBuilder();
                for (Property<?> entry : state.getProperties()) {
                    if(trulyrandom$ignoreStateProperties()) continue;

                    variant.append(state.getValue(entry));
                }
                if(!trulyrandom$ignoreModelOcclusion()) {
                    variant.append(state.getOcclusionShape());
                }
                propertyMap.computeIfAbsent(variant.toString(), _ -> new ArrayList<>()).add(state);
            }
            propertyMap.forEach((_, v) -> v.sort(Comparator.comparingInt(state -> BuiltInRegistries.BLOCK.getId(state.getBlock()))));
            return propertyMap;
        }
    }

    interface Items extends ModelShuffler<Identifier> {
    }
}
