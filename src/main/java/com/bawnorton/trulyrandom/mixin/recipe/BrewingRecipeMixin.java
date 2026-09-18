//? if >=26.3 {
package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BrewingRecipe.class)
abstract class BrewingRecipeMixin implements ResultClearer {
    @Unique
    private ItemStack result = ItemStack.EMPTY;

    @ModifyReturnValue(
            method = "assemble(Lnet/minecraft/world/item/crafting/BrewingInput;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private ItemStack useRandomResult(ItemStack result) {
        if (this.result.isEmpty()) return result;

        return this.result.copy();
    }

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = result;
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        return result;
    }
}
//?}