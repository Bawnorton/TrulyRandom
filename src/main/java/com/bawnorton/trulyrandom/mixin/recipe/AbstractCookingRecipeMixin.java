package com.bawnorton.trulyrandom.mixin.recipe;

import com.bawnorton.trulyrandom.extend.ResultSetter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractCookingRecipe.class)
public abstract class AbstractCookingRecipeMixin implements ResultSetter {
    @Shadow @Final @Mutable
    protected ItemStack output;

    @Override
    public void trulyrandom$setResult(ItemStack result) {
        output = result;
    }
}
