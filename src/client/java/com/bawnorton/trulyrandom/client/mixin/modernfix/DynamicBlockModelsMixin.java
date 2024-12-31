package com.bawnorton.trulyrandom.client.mixin.modernfix;

import com.bawnorton.mixinsquared.TargetHandler;
import com.bawnorton.trulyrandom.client.extend.modernfix.DynamicBlockModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.Map;

@Mixin(value = BlockModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(min = "5.11"))
public abstract class DynamicBlockModelsMixin implements DynamicBlockModelShuffler {
    @Unique
    private final UnaryMap<BlockState> trulyrandom$redirectMap = new UnaryHashMap<>();

    @Shadow private Map<BlockState, BakedModel> models;

    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.BlockModelShaperMixin",
            name = "method_3335"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/render/block/BlockModels.cacheBlockModel (Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private BakedModel getShuffledModel(BlockModels instance, BlockState blockState, Operation<BakedModel> original) {
        BlockState redirected = trulyrandom$redirectMap.getOrDefault(blockState, blockState);
        return original.call(instance, redirected);
    }

    public UnaryMap<BlockState> trulyrandom$getRedirectMap() {
        return trulyrandom$redirectMap;
    }

    public void trulyrandom$resetModels() {
        trulyrandom$redirectMap.keySet().forEach(key -> models.put(key, null));
        trulyrandom$redirectMap.clear();
    }

    public boolean trulyrandom$isShuffled() {
        return !trulyrandom$redirectMap.isEmpty();
    }
}
