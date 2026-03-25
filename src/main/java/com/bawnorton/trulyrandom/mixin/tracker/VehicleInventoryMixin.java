package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(VehicleInventory.class)
public interface VehicleInventoryMixin {
    @Inject(method = "generateInventoryLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/ResourceKey;)Lnet/minecraft/loot/LootTable;"))
    default void trackCause(PlayerEntity player, CallbackInfo ci) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(player == null) return;
        if(player.getWorld().isClient()) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam()));
    }
}
