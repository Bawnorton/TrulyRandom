package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.context.ContextParameter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MatchToolLootCondition.class)
public abstract class MatchToolLootConditionMixin {

    @WrapOperation(
            method = "test(Lnet/minecraft/loot/context/LootContext;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/loot/context/LootContext;get(Lnet/minecraft/util/context/ContextParameter;)Ljava/lang/Object;"
            )
    )
    private <T> T checkKillingEntityForTool(LootContext instance, ContextParameter<T> parameter, Operation<T> original) {
        T stack = original.call(instance, parameter);
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return stack;
        if(stack != null) return stack;
        if(!instance.hasParameter(LootContextParameters.ATTACKING_ENTITY)) return null;

        Entity attacker = instance.get(LootContextParameters.ATTACKING_ENTITY);
        if (!(attacker instanceof LivingEntity livingEntity)) return null;

        //noinspection unchecked
        return (T) livingEntity.getMainHandStack();
    }
}
