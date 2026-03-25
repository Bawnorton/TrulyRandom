package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(PiglinBrain.class)
public abstract class PiglinBrainMixin {
    @Inject(method = "getBarteredItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/ResourceKey;)Lnet/minecraft/loot/LootTable;"))
    private static void trackCause(PiglinEntity piglin, CallbackInfoReturnable<List<ItemStack>> cir) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if (piglin.getWorld().isClient()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        piglin.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).ifPresent(player -> LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam())));
    }
}
