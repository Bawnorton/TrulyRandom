package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
//import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
//import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(BlockModels.class)
//@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, invert = true)
public abstract class VanillaBlockModelsMixin implements ModelShuffler.BlockStates {
    @Unique
    private final UnaryMap<BlockState> trulyrandom$redirectMap = new UnaryHashMap<>();
    @Shadow
    private Map<BlockState, BlockStateModel> models;

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "java/util/Map.get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object getShuffledModel(Map<BlockState, BlockStateModel> instance, Object key, Operation<Object> original) {
        BlockState redirected = trulyrandom$redirectMap.getOrDefault((BlockState) key, (BlockState) key);
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
                trulyrandom$redirectMap.put(original, randomised);
            }
        }
    }

    @Override
    public UnaryMap<BlockState> trulyrandom$getRedirectMap() {
        return trulyrandom$redirectMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        trulyrandom$redirectMap.clear();
    }

    @Override
    public boolean trulyrandom$isShuffled() {
        return !trulyrandom$redirectMap.isEmpty();
    }
}
