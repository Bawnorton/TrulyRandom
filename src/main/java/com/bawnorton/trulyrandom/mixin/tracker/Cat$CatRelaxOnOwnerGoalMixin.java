package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

@Mixin(targets = "net.minecraft.world.entity.animal.feline.Cat$CatRelaxOnOwnerGoal")
abstract class Cat$CatRelaxOnOwnerGoalMixin {
    @Shadow
    @Final
    private Cat cat;

    @Shadow
    private @Nullable Player ownerPlayer;

    @Inject(
            method = "giveMorningGift",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/feline/Cat;dropFromGiftLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/BiConsumer;)Z"
            )
    )
    private void trackCause(CallbackInfo ci) {
        if (!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;
        if (cat.level().isClientSide()) return;

        LootTableTracker.LOOT_CAUSERS.remove();
        if(ownerPlayer == null) return;

        LootTableTracker.LOOT_CAUSERS.set(List.of(ownerPlayer.trulyrandom$getTeam()));
    }
}
