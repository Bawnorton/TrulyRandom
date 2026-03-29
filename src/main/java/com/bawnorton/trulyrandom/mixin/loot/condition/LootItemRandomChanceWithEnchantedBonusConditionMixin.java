package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemRandomChanceWithEnchantedBonusCondition.class)
abstract class LootItemRandomChanceWithEnchantedBonusConditionMixin {
    @ModifyReturnValue(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At("RETURN")
    )
    private boolean alwaysPass(boolean original) {
        return original || TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES);
    }
}
