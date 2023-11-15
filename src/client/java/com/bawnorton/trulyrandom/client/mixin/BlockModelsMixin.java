package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.StateAccessor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(BlockModels.class)
public abstract class BlockModelsMixin implements ModelShuffler.BlockStates {
    @Unique
    private final Map<BlockState, BakedModel> shuffledModels = new HashMap<>();
    @Unique
    private final Map<BlockState, BlockState> originalToRandomMap = new HashMap<>();
    @Shadow
    private Map<BlockState, BakedModel> models;

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object getShuffledModel(Map<BlockState, BakedModel> instance, Object key, Operation<Object> original) {
        return shuffledModels.getOrDefault((BlockState) key, (BakedModel) original.call(instance, key));
    }

    @Override
    public void trulyrandom$shuffleModels(Random rnd) {
        if (models == null) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        List<BlockState> blockStates = new ArrayList<>(models.keySet());
        trulyrandom$resetModels();
        Map<String, List<BlockState>> propertyMap = new HashMap<>();
        for(BlockState state: blockStates) {
            StringBuilder variant = new StringBuilder();
            for(Map.Entry<Property<?>, Comparable<?>> entry: state.getEntries().entrySet()) {
                variant.append(StateAccessor.getPropertyMapPrinter().apply(entry));
            }
            variant.append(state.isOpaque());
            variant.append(state.getCullingShape(world, BlockPos.ORIGIN));
            propertyMap.computeIfAbsent(variant.toString(), k -> new ArrayList<>()).add(state);
        }
        propertyMap.forEach((k, v) -> v.sort(Comparator.comparingInt(state -> Registries.BLOCK.getRawId(state.getBlock()))));
        Map<BlockState, Float> originalChanceMap = new HashMap<>();
        for(List<BlockState> variant: propertyMap.values()) {
            Collections.shuffle(variant, rnd);
            for(int i = 0; i < variant.size(); i++) {
                BlockState original = variant.get(i);
                BlockState randomised = variant.get((i + 1) % variant.size());
                originalChanceMap.putIfAbsent(original, 1f / variant.size());
                originalToRandomMap.put(original, randomised);
                shuffledModels.put(original, models.get(randomised));
            }
        }
        originalChanceMap.forEach((k, v) -> {
            if(rnd.nextFloat() < v) shuffledModels.put(k, models.get(k));
        });
    }

    @Override
    public Map<BlockState, BlockState> trulyrandom$getOriginalRandomisedMap() {
        return originalToRandomMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        shuffledModels.clear();
        originalToRandomMap.clear();
    }

    @Override
    public boolean trulyRandom$isShuffled() {
        return !shuffledModels.isEmpty();
    }
}
