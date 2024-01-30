package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.mixin.accessor.StateAccessor;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
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
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, invert = true)
public abstract class VanillaBlockModelsMixin implements ModelShuffler.BlockStates {
    @Shadow
    private Map<BlockState, BakedModel> models;
    @Unique
    private final Map<BlockState, BlockState> originalToRandomMap = new HashMap<>();
    @Unique
    private final Map<BlockState, BakedModel> shuffledModels = new HashMap<>();

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object getShuffledModel(Map<BlockState, BakedModel> instance, Object key, Operation<Object> original) {
        return shuffledModels.getOrDefault((BlockState) key, (BakedModel) original.call(instance, key));
    }

    @Override
    public List<BlockState> trulyrandom$getBlockStates() {
        return new ArrayList<>(models.keySet());
    }

    @Override
    public void trulyrandom$shuffleModels(long seed) {
        if (models == null) return;
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        trulyrandom$resetModels();

        Random rnd = new Random(seed);
        for (List<BlockState> variant : buildPropertyMap().values()) {
            Collections.shuffle(variant, rnd);
            for (int i = 0; i < variant.size(); i++) {
                BlockState original = variant.get(i);
                BlockState randomised = variant.get((i + 1) % variant.size());
                originalToRandomMap.put(original, randomised);
                shuffledModels.put(original, models.get(randomised));
            }
        }
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
    public boolean trulyrandom$isShuffled() {
        return !shuffledModels.isEmpty();
    }
}
