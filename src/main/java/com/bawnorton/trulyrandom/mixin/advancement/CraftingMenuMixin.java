package com.bawnorton.trulyrandom.mixin.advancement;

import com.bawnorton.trulyrandom.registry.TrulyRandomCriteria;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.crafting.CraftingInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CraftingMenu.class)
abstract class CraftingMenuMixin {
    @ModifyExpressionValue(
            method = "slotChangedCraftingGrid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/CraftingContainer;asCraftInput()Lnet/minecraft/world/item/crafting/CraftingInput;"
            )
    )
    private static CraftingInput checkInput(CraftingInput original, @Local(argsOnly = true) Player player) {
        TrulyRandomCriteria.CRAFTING.trigger((ServerPlayer) player, original);
        return original;
    }
}
