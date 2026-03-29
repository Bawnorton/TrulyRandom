package com.bawnorton.trulyrandom.mixin.advancement;

import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerEntityMixin {
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/advancements/criterion/PlayerTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;)V"
            )
    )
    private void triggerModuleEnabled(CallbackInfo ci) {
        TrulyRandomCriteria.MODULE_ENABLED.trigger((ServerPlayer) (Object) this);
    }
}
