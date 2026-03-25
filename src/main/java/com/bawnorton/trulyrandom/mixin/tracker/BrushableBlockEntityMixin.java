package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
    @Inject(method = "generateItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/ResourceKey;)Lnet/minecraft/loot/LootTable;"))
    private void trackCause(ServerWorld world, LivingEntity brusher, ItemStack brush, CallbackInfo ci) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if (brusher == null) return;
        if (brusher.getWorld().isClient()) return;
        if (!(brusher instanceof PlayerEntity player)) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(player.trulyrandom$getTeam()));
    }
}
