package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(BlockModels.class)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, invert = true)
public abstract class VanillaBlockModelsMixin implements ModelShuffler.BlockStates {
    @Unique
    private final Map<BlockState, BlockState> redirectMap = new HashMap<>();
    @Shadow
    private Map<BlockState, BakedModel> models;

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object getShuffledModel(Map<BlockState, BakedModel> instance, Object key, Operation<Object> original) {
        BlockState redirected = redirectMap.getOrDefault((BlockState) key, (BlockState) key);
        return original.call(instance, redirected);
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
                redirectMap.put(original, randomised);
            }
        }
    }

    @Override
    public Map<BlockState, BlockState> trulyrandom$getOriginalRandomisedMap() {
        return redirectMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        redirectMap.clear();
    }

    @Override
    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
