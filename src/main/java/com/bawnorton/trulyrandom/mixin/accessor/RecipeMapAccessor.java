package com.bawnorton.trulyrandom.mixin.accessor;

import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(RecipeMap.class)
public interface RecipeMapAccessor {
    @Accessor("byKey")
    Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> trulyrandom$byKey();

    @Accessor("byKey") @Mutable
    void trulyrandom$byKey(Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byKey);

    @Accessor("byType") @Mutable
    void trulyrandom$byType(Multimap<RecipeType<?>, RecipeHolder<?>> byType);
}
