package com.bawnorton.trulyrandom.client.mixin.modernfix.v511plus;

import com.bawnorton.mixinsquared.TargetHandler;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.extend.modernfix.DynamicItemModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.HashMap;
import java.util.Map;

@Debug(export = true)
@Mixin(value = ItemModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(min = "5.11"))
public abstract class DynamicItemsModelsMixin implements DynamicItemModelShuffler {
    @Unique
    private final Map<Item, Item> redirectMap = new HashMap<>();

    @Shadow public abstract void reloadModels();

    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.ItemModelShaperMixin",
            name = "lambda$new$0"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/render/item/ItemModels.mfix$getModelForItem (Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private BakedModel getShuffledModel(ItemModels instance, Item item, Operation<BakedModel> original) {
        Item redirected = redirectMap.getOrDefault(item, item);
        return original.call(instance, redirected);
    }

    public Map<Item, Item> trulyrandom$getRedirectMap() {
        return redirectMap;
    }

    public void trulyrandom$resetModels() {
        redirectMap.clear();
        reloadModels();
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
