package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.recipe.TransmuteRecipeResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin implements ResultHolder {
    @Mutable
    @Final
    @Shadow
    TransmuteRecipeResult result;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = new TransmuteRecipeResult(
                result.getRegistryEntry(),
                result.getCount(),
                result.getComponentChanges()
        );
    }

    @Override
    public ItemStack trulyrandom$getResult() {
        Item item = result.itemEntry().value();
        ItemStack stack = new ItemStack(item, result.count());
        stack.applyUnvalidatedChanges(result.components());
        return stack;
    }
}
