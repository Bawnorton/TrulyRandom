package com.bawnorton.trulyrandom.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(PreparedRecipes.class)
public interface PreparedRecipesAccessor {
    @Accessor
    Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> getByKey();

    @Accessor @Mutable
    void setByKey(Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey);

    @Accessor @Mutable
    void setByType(Multimap<RecipeType<?>, RecipeHolder<?>> byType);
}
