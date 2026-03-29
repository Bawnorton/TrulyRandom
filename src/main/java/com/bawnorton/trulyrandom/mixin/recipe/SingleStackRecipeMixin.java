package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleItemRecipe.class)
abstract class SingleStackRecipeMixin implements ResultHolder {
    @Shadow @Final @Mutable
    private ItemStack result;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = result;
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        return result;
    }
}
