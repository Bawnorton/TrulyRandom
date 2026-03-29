package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(PiglinAi.class)
abstract class PiglinAiMixin {
    @Inject(
            method = "getBarterResponseItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"
            )
    )
    private static void trackCause(Piglin piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if (piglin.level().isClientSide()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        piglin.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).ifPresent(player -> LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam())));
    }
}
