package com.bawnorton.trulyrandom.client.mixin.modernfix.v511plus;

import com.bawnorton.mixinsquared.TargetHandler;
import com.bawnorton.trulyrandom.client.extend.modernfix.DynamicBlockModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import org.embeddedt.modernfix.dynamicresources.DynamicModelCache;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(value = BlockModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(min = "5.11"))
public abstract class DynamicBlockModelsMixin implements DynamicBlockModelShuffler {
    @Unique
    private final Map<BlockState, BlockState> redirectMap = new HashMap<>();

    @SuppressWarnings("MixinAnnotationTarget")
    @Shadow
    @Final
    private DynamicModelCache<BlockState> mfix$modelCache;

    @TargetHandler(
            mixin = "org.embeddedt.modernfix.common.mixin.perf.dynamic_resources.BlockModelShaperMixin",
            name = "lambda$new$0"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/client/render/block/BlockModels.cacheBlockModel (Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/model/BakedModel;"
            )
    )
    private BakedModel getShuffledModel(BlockModels instance, BlockState blockState, Operation<BakedModel> original) {
        BlockState redirected = redirectMap.getOrDefault(blockState, blockState);
        return original.call(instance, redirected);
    }

    public Map<BlockState, BlockState> trulyrandom$getOriginalRandomisedMap() {
        return redirectMap;
    }

    public void trulyrandom$resetModels() {
        redirectMap.clear();
        mfix$modelCache.clear();
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
