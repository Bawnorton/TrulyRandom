package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.condition.LocationCheckLootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.function.Predicate;

@Mixin(LootPoolEntry.class)
public abstract class LootPoolEntryMixin {
    @WrapOperation(
            method = "test",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z",
                    remap = false
            )
    )
    private <T> boolean ignoreConditionsOnLootRandomiser(Predicate<T> instance, Object context, Operation<Boolean> original) {
        boolean isRandomised = TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES);
        if(!isRandomised) return original.call(instance, context);

        return switch (instance) {
            case BlockStatePropertyLootCondition ignored -> true;
            case LocationCheckLootCondition ignored -> true;
            default -> original.call(instance, context);
        };
    }
}
