package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(targets = "net.minecraft.entity.passive.CatEntity$SleepWithOwnerGoal")
public abstract class CatEntity$SleepWithOwnerGoalMixin {
    @Shadow private @Nullable PlayerEntity owner;

    @Shadow @Final private CatEntity cat;

    @Inject(method = "dropMorningGifts", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/loot/LootTable;"))
    private void trackCause(CallbackInfo ci) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if (cat.getWorld().isClient()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        if(owner == null) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(owner.getUuid()));
    }
}
