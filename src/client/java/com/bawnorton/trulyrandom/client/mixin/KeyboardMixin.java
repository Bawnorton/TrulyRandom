package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.keybind.KeybindManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getNarratorManager()Lnet/minecraft/client/util/NarratorManager;"))
    private void captureKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if(KeybindManager.NEW_SEED != null && KeybindManager.NEW_SEED.getKeybind().matchesKey(key, scancode)) {
            KeybindManager.NEW_SEED.invokeAction(client);
        }
    }
}
