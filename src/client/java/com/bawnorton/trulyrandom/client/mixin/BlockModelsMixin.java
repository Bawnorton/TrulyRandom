package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.util.BlockStateVariant;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(BlockModels.class)
public abstract class BlockModelsMixin implements ModelShuffler {
    @Shadow
    private Map<BlockState, BakedModel> models;

    @Unique
    private final Map<BlockState, BakedModel> shuffledModels = new HashMap<>();

    @Unique
    private final Map<String, List<BlockState>> variantMap = new HashMap<>();

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object redirectGetModel(Map<BlockState, BakedModel> instance, Object key, Operation<Object> original) {
        return shuffledModels.getOrDefault((BlockState) key, (BakedModel) original.call(instance, key));
    }

    @Override
    public void trulyrandom$shuffleModels(Random rnd) {
        if (models == null) return;
        if (MinecraftClient.getInstance().world == null) return;

        List<BlockStateVariant> properties = models.keySet().stream().map(BlockStateVariant::new).toList();
        if (variantMap.isEmpty()) {
            for (BlockStateVariant stateVariant : properties) {
                variantMap.putIfAbsent(stateVariant.variant(), new ArrayList<>());
                variantMap.get(stateVariant.variant()).add(stateVariant.state());
            }
        }
        if (variantMap.isEmpty()) return;

        for (List<BlockState> list : variantMap.values()) {
            Collections.shuffle(list, rnd);
        }
        shuffledModels.clear();
        for (BlockStateVariant stateVariant : properties) {
            List<BlockState> list = variantMap.get(stateVariant.variant());
            if (list == null || list.isEmpty()) {
                shuffledModels.put(stateVariant.state(), models.get(stateVariant.state()));
                continue;
            }
            int index = list.indexOf(stateVariant.state());
            BlockState newState = list.get((index + 1) % list.size());
            shuffledModels.put(newState, models.get(stateVariant.state()));
        }
    }
}
