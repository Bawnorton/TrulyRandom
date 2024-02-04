package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(ItemModels.class)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, invert = true)
public abstract class VanillaItemsModelsMixin implements ModelShuffler.Items {
    @Unique
    private final Map<Item, Item> redirectMap = new HashMap<>();
    @Shadow @Final
    private Int2ObjectMap<BakedModel> models;


    @WrapOperation(method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModelId(Lnet/minecraft/item/Item;)I", remap = false))
    private int getShuffledModel(Item item, Operation<Integer> original) {
        Item redirected = redirectMap.getOrDefault(item, item);
        return original.call(redirected);
    }

    @Override
    public void trulyrandom$shuffleModels(long seed) {
        if (models == null) return;

        List<Item> items = Registries.ITEM.stream()
                                          .sorted(Comparator.comparingInt(Registries.ITEM::getRawId))
                                          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        trulyrandom$resetModels();
        Collections.shuffle(items, new Random(seed));
        for (int i = 0; i < items.size(); i++) {
            Item originalItem = items.get(i);
            Item randomItem = items.get((i + 1) % items.size());
            redirectMap.put(originalItem, randomItem);
        }
    }

    @Override
    public Map<Item, Item> trulyrandom$getRedirectMap() {
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
