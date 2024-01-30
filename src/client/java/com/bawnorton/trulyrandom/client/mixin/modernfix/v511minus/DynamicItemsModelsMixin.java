package com.bawnorton.trulyrandom.client.mixin.modernfix.v511minus;

import com.bawnorton.trulyrandom.client.extend.modernfix.DynamicItemModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.item.Item;
import org.embeddedt.modernfix.dynamicresources.ModelLocationCache;
import org.embeddedt.modernfix.util.DynamicInt2ObjectMap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Debug(export = true)
@Mixin(value = ItemModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(max = "5.10.1"))
public abstract class DynamicItemsModelsMixin implements DynamicItemModelShuffler {
    @Unique
    private final Map<Item, Item> redirectMap = new HashMap<>();

    @Mutable
    @Shadow @Final private Int2ObjectMap<BakedModel> models;

    @Shadow public abstract BakedModelManager getModelManager();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void trulyrandom$init(CallbackInfo ci) {
        trulyrandom$resetModels();
    }

    @ModifyVariable(method = "getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;", at = @At("HEAD"), argsOnly = true)
    private Item redirectModel(Item item) {
        return redirectMap.getOrDefault(item, item);
    }

    public Map<Item, Item> trulyrandom$getOriginalRandomisedMap() {
        return redirectMap;
    }

    public void trulyrandom$resetModels() {
        redirectMap.clear();
        models = new DynamicInt2ObjectMap<>(index -> {
            Item item = Item.byRawId(index);
            return getModelManager().getModel(ModelLocationCache.get(redirectMap.getOrDefault(item, item)));
        });
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
