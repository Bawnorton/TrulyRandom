package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.GiveGiftsToHeroTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(GiveGiftsToHeroTask.class)
public abstract class GiveGiftsToHeroTaskMixin {
    @Inject(method = "giveGifts", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/GiveGiftsToHeroTask;getGifts(Lnet/minecraft/entity/passive/VillagerEntity;)Ljava/util/List;"))
    private void trackCause(VillagerEntity villager, LivingEntity recipient, CallbackInfo ci) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if(recipient.getWorld().isClient()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        if(recipient instanceof PlayerEntity player) {
            LootTableTracker.LOOT_CAUSERS.set(List.of(player.getUuid()));
        }
    }
}
