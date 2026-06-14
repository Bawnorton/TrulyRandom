package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MatchTool.class)
abstract class MatchToolMixin {
    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootContext;getOptionalParameter(Lnet/minecraft/util/context/ContextKey;)Ljava/lang/Object;"
            )
    )
    private <T> T checkKillingEntityForTool(LootContext instance, ContextKey<T> key, Operation<T> original) {
        T stack = original.call(instance, key);
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return stack;
        if(stack != null) return stack;
        if(!instance.hasParameter(LootContextParams.ATTACKING_ENTITY)) return null;

        Entity attacker = instance.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
        if (!(attacker instanceof LivingEntity livingEntity)) return null;

        return (T) livingEntity.getMainHandItem();
    }
}
