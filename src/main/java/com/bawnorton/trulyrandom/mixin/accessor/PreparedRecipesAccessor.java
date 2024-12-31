package com.bawnorton.trulyrandom.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(PreparedRecipes.class)
public interface PreparedRecipesAccessor {
    @Accessor
    Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> getByKey();

    @Accessor @Mutable
    void setByKey(Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> byKey);

    @Accessor @Mutable
    void setByType(Multimap<RecipeType<?>, RecipeEntry<?>> byType);
}
