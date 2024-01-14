package com.bawnorton.trulyrandom.client.mixin;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    /*@Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getNarratorManager()Lnet/minecraft/client/util/NarratorManager;"))
    private void captureKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if(KeybindManager.NEW_SEED != null && KeybindManager.NEW_SEED.getKeybind().matchesKey(key, scancode)) {
            KeybindManager.NEW_SEED.run(client);
        }
    }*/
}
