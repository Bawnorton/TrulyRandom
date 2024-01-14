package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Accessor
    Map<Identifier, Recipe<?>> getRecipesById();
}
