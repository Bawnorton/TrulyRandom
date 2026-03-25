package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import java.util.Map;

@Mixin(ServerRecipeManager.class)
public interface ServerRecipeManagerAccessor {
    @Accessor
    PreparedRecipes getPreparedRecipes();
}
