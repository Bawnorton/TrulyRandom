package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultSetter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin implements ResultSetter {
    @Mutable
    @Final
    @Shadow
    ItemStack result;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        this.result = result;
    }
}
