package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(SnifferEntity.class)
public abstract class SnifferEntityMixin extends AnimalEntity {
    protected SnifferEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropSeeds", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/loot/LootTable;"))
    private void trackCause(CallbackInfo ci) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(getWorld().isClient()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        PlayerEntity nearest = getWorld().getClosestPlayer(this, 32D);
        if(nearest == null) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(nearest.getUuid()));
    }
}
