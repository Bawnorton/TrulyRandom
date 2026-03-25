package com.bawnorton.trulyrandom.mixin.advancement;

import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.server.network.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {
    @ModifyExpressionValue(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/inventory/RecipeInputInventory;createRecipeInput()Lnet/minecraft/recipe/input/CraftingRecipeInput;"
            )
    )
    private static CraftingRecipeInput checkInput(CraftingRecipeInput original, @Local(argsOnly = true) PlayerEntity player) {
        TrulyRandomCriteria.CRAFTING.trigger((ServerPlayer) player, original);
        return original;
    }
}
