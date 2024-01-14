package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultSetter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapelessRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ShapelessRecipe.class)
public abstract class ShapelessRecipeMixin implements ResultSetter {
    @Shadow @Final @Mutable
    ItemStack output;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        output = result;
    }
}
