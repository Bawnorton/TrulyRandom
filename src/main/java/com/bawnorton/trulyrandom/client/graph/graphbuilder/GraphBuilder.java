package com.bawnorton.trulyrandom.client.graph.graphbuilder;

import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Set;

public interface GraphBuilder<T> {
    default GraphElement build(T t, LootTableTracker lootTracker, RecipeTracker recipeTracker) {
        return build(t, lootTracker, recipeTracker, new HashSet<>(), new HashSet<>());
    }

    GraphElement build(T t, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes);
}

