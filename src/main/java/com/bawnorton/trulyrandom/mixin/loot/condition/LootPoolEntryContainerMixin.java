package com.bawnorton.trulyrandom.mixin.loot.condition;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.function.Predicate;

@Mixin(LootPoolEntryContainer.class)
abstract class LootPoolEntryContainerMixin {
    @WrapOperation(
            method = "canRun",
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
            case LootItemBlockStatePropertyCondition ignored -> true;
            case LocationCheck ignored -> true;
            default -> original.call(instance, context);
        };
    }
}
