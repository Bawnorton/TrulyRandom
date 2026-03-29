package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {
    @Accessor("template")
    Ingredient trulyrandom$template();

    @Accessor("base")
    Ingredient trulyrandom$base();

    @Accessor("addition")
    Ingredient trulyrandom$addition();

}
