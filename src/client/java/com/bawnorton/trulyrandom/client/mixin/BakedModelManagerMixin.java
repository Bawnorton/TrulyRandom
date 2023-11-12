package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin implements ModelShuffler {
    @Shadow private Map<Identifier, BakedModel> models;

    @Unique
    private final Map<Identifier, BakedModel> shuffledModels = new HashMap<>();

    @Unique
    private final Map<String, List<ModelIdentifier>> variantMap = new HashMap<>();

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Ljava/util/Map;getOrDefault(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object redirectGetModel(Map<Identifier, BakedModel> instance, Object key, Object defaultValue, Operation<Object> original) {
        return shuffledModels.getOrDefault((Identifier) key, (BakedModel) original.call(instance, key, defaultValue));
    }

    @Override
    public void trulyrandom$shuffleModels(Random rnd) {
        if(models == null) return;

        List<ModelIdentifier> modelIds = models.keySet().stream().map(id -> (ModelIdentifier) id).toList();
        if(variantMap.isEmpty()) {
            for (ModelIdentifier modelId : modelIds) {
                String variant = modelId.getVariant();
                variantMap.putIfAbsent(variant, new ArrayList<>());
                variantMap.get(variant).add(modelId);
            }
        }
        if (variantMap.isEmpty()) return;

        for (List<ModelIdentifier> list : variantMap.values()) {
            Collections.shuffle(list, rnd);
        }
        shuffledModels.clear();
        for (ModelIdentifier modelId : modelIds) {
            String variant = modelId.getVariant();
            List<ModelIdentifier> list = variantMap.get(variant);
            if (list == null || list.isEmpty()) {
                shuffledModels.put(modelId, models.get(modelId));
                continue;
            }
            int index = list.indexOf(modelId);
            ModelIdentifier newModelId = list.get((index + 1) % list.size());
            shuffledModels.put(newModelId, models.get(modelId));
        }
    }
}
