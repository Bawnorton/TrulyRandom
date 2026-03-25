package com.bawnorton.trulyrandom.client.graph.graphbuilder;

import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.ResourceKey;
import java.util.HashSet;
import java.util.Set;

public interface GraphBuilder<T> {
    default GraphElement build(T t, LootTableTracker lootTracker, RecipeTracker recipeTracker) {
        return build(t, lootTracker, recipeTracker, new HashSet<>(), new HashSet<>());
    }

    GraphElement build(T t, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes);
}

