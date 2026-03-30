package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SingleItemRecipe.class)
abstract class SingleStackRecipeMixin implements ResultHolder {
    @Shadow @Final @Mutable
    private ItemStackTemplate result;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = ItemStackTemplate.fromNonEmptyStack(result);
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        return result.create();
    }
}
