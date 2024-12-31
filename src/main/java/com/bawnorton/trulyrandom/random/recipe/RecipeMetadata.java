package com.bawnorton.trulyrandom.random.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryKey;

public record RecipeMetadata(RegistryKey<Recipe<?>> key, RecipeEntry<?> entry) {
    public Recipe<?> recipe() {
        return entry.value();
    }

    public RecipeType<?> type() {
        return recipe().getType();
    }
}
