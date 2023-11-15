package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(ItemModels.class)
public abstract class ItemsModelsMixin implements ModelShuffler.Items {
    @Unique
    private final Map<Item, Item> originalToRandomMap = new HashMap<>();
    @Final
    @Shadow
    private Int2ObjectMap<BakedModel> models;
    @Unique
    private Int2ObjectMap<BakedModel> shuffledModels = new Int2ObjectOpenHashMap<>(256);

    @WrapOperation(method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;get(I)Ljava/lang/Object;"))
    private Object getShuffledModel(Int2ObjectMap<BakedModel> instance, int key, Operation<Object> original) {
        return shuffledModels.getOrDefault(key, (BakedModel) original.call(instance, key));
    }

    @Override
    public void trulyrandom$shuffleModels(Random rnd) {
        if (models == null) return;

        List<Integer> modelIds = new ArrayList<>(models.keySet());
        trulyrandom$resetModels();
        Collections.shuffle(modelIds, rnd);
        for (int i = 0; i < modelIds.size(); i++) {
            int originalId = modelIds.get(i);
            int randomId = modelIds.get((i + 1) % modelIds.size());
            BakedModel shuffledModel = models.get(randomId);
            shuffledModels.put(originalId, shuffledModel);
            originalToRandomMap.put(Item.byRawId(originalId), Item.byRawId(randomId));
        }
    }

    @Unique
    @Override
    public Map<Item, Item> trulyrandom$getOriginalRandomisedMap() {
        return originalToRandomMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        shuffledModels = new Int2ObjectOpenHashMap<>(models.size());
        originalToRandomMap.clear();
    }

    @Override
    public boolean trulyRandom$isShuffled() {
        return !shuffledModels.isEmpty();
    }
}
