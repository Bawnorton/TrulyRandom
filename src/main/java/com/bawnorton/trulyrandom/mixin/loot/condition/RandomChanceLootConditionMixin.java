package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomChanceLootCondition.class)
public abstract class RandomChanceLootConditionMixin {
    @ModifyReturnValue(method = "test(Lnet/minecraft/loot/context/LootContext;)Z", at = @At("RETURN"))
    private boolean alwaysPass(boolean original) {
        return TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES);
    }
}
