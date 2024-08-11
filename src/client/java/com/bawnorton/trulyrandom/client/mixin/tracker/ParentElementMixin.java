package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.extend.InventoryScreenExtender;
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParentElement.class)
public interface ParentElementMixin {
    @Inject(
            method = "mouseScrolled",
            at = @At("HEAD"),
            cancellable = true
    )
    default void mouseScrolledInInvScreen(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        if(this instanceof InventoryScreenExtender invScreen) {
            if(invScreen.trulyrandom$getLootBook().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                cir.setReturnValue(true);
            }
        }
    }
}
