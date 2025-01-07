package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(
            method = "onCraftByPlayer",
            at = @At("HEAD")
    )
    private void trackCraft(CallbackInfo ci) {
        RecipeTracker.LAST_RECIPE_OUTPUT.set((ItemStack) (Object) this);
    }
}
