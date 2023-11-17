package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.extend.ResultSetter;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({
        ShapedRecipe.class,
        ShapelessRecipe.class,
        AbstractCookingRecipe.class,
        CuttingRecipe.class,
        SmithingTransformRecipe.class,
})
public abstract class RecipeMixin implements ResultSetter {
    @Mutable
    @Final
    @Shadow(remap = false, aliases = {
            "field_9050",
            "field_9053",
            "field_9059",
            "field_17643",
            "field_42033"
    }) // don't ask
    ItemStack result;

    @Override
    public void trulyRandom$setResult(ItemStack result) {
        this.result = result;
    }
}
