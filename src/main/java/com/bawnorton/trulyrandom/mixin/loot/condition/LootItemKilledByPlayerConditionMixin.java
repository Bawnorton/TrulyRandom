package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootItemKilledByPlayerCondition.class)
abstract class LootItemKilledByPlayerConditionMixin {
    @ModifyReturnValue(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At("RETURN")
    )
    private boolean removeRequirements(boolean original) {
        return original || TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES);
    }
}
