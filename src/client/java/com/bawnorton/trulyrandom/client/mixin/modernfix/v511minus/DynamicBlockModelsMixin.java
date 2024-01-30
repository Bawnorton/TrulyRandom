package com.bawnorton.trulyrandom.client.mixin.modernfix.v511minus;

import com.bawnorton.trulyrandom.client.extend.modernfix.DynamicBlockModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.VersionPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import org.embeddedt.modernfix.dynamicresources.ModelLocationCache;
import org.embeddedt.modernfix.util.DynamicOverridableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = BlockModels.class, priority = 1500)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, version = @VersionPredicate(max = "5.10.1"))
public abstract class DynamicBlockModelsMixin implements DynamicBlockModelShuffler {
    @Shadow
    private Map<BlockState, BakedModel> models;
    @Shadow
    @Final
    private BakedModelManager modelManager;
    @Unique
    private final Map<BlockState, BlockState> redirectMap = new HashMap<>();

    @Inject(
            method = {
                    "<init>",
                    "setModels"
            },
            at = @At("RETURN")
    )
    private void trulyrandom$initModels(CallbackInfo ci) {
        trulyrandom$resetModels();
    }

    @ModifyVariable(method = "getModel", at = @At("HEAD"), argsOnly = true)
    private BlockState trulyrandom$redirectModel(BlockState state) {
        return redirectMap.getOrDefault(state, state);
    }

    public Map<BlockState, BlockState> trulyrandom$getOriginalRandomisedMap() {
        return redirectMap;
    }

    public void trulyrandom$resetModels() {
        redirectMap.clear();
        models = new DynamicOverridableMap<>(state -> {
            state = redirectMap.getOrDefault(state, state);
            return modelManager.getModel(ModelLocationCache.get(state));
        });
    }

    public boolean trulyrandom$isShuffled() {
        return !redirectMap.isEmpty();
    }
}
