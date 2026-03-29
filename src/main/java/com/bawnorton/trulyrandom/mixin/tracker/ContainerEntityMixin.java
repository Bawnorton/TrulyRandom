package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(ContainerEntity.class)
interface ContainerEntityMixin {
    @Inject(method = "unpackChestVehicleLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/ContainerEntity;getContainerLootTable()Lnet/minecraft/resources/ResourceKey;"))
    default void trackCause(Player player, CallbackInfo ci) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(player == null) return;
        if(player.level().isClientSide()) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam()));
    }
}
