package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ItemCriterionCapture;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocationCheckLootCondition.class)
public abstract class LocationCheckLootConditionMixin {
    @ModifyReturnValue(method = "test(Lnet/minecraft/loot/context/LootContext;)Z", at = @At("RETURN"))
    private boolean removeRequirements(boolean original) {
        ItemCriterion triggeredCriterion = ItemCriterionCapture.TRIGGERD_CRITERION.get();
        if(triggeredCriterion == Criteria.PLACED_BLOCK) return original;
        if(triggeredCriterion == Criteria.ITEM_USED_ON_BLOCK) return original;

        return TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES);
    }
}
