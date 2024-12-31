package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Optional;

@Mixin(SmithingTrimRecipe.class)
public interface SmithingTrimRecipeAccessor {
    @Accessor
    Optional<Ingredient> getTemplate();

    @Accessor
    Optional<Ingredient> getBase();

    @Accessor
    Optional<Ingredient> getAddition();

}
