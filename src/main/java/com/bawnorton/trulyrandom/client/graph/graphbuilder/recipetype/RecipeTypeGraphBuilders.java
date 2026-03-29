package com.bawnorton.trulyrandom.client.graph.graphbuilder.recipetype;

import com.bawnorton.trulyrandom.client.graph.element.*;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.GraphBuilder;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.droptype.DropTypeGraphBuilders;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.drop.TrackingConnection;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.Stats;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecipeTypeGraphBuilders {
    private static final Map<RecipeType<?>, GraphBuilder<RecipeHolder<?>>> GRAPH_BUILDERS = new HashMap<>() {
        @Override
        public GraphBuilder<RecipeHolder<?>> put(RecipeType<?> key, GraphBuilder<RecipeHolder<?>> value) {
            return super.put(key, ForwardingRecipeTypeGraphBuilder.forward(value));
        }
    };

    static {
        GRAPH_BUILDERS.put(RecipeType.CRAFTING, RecipeTypeGraphBuilders::craftingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.BLASTING, RecipeTypeGraphBuilders::blastingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.SMELTING, RecipeTypeGraphBuilders::smeltingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.CAMPFIRE_COOKING, RecipeTypeGraphBuilders::campfireCookingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.SMITHING, RecipeTypeGraphBuilders::smithingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.STONECUTTING, RecipeTypeGraphBuilders::stonecuttingGraphBuilder);
        GRAPH_BUILDERS.put(RecipeType.SMOKING, RecipeTypeGraphBuilders::smokingGraphBuilder);
    }

    public static GraphBuilder<RecipeHolder<?>> getBuilder(RecipeType<?> recipeType) {
        return GRAPH_BUILDERS.getOrDefault(recipeType, (ForwardingRecipeTypeGraphBuilder) (_, _) -> null);
    }

    public static void addDirect(LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes, RecipeHolder<?> recipe, GraphElement root) {
        GraphElement element = RecipeTypeGraphBuilders.getBuilder(recipe.value().getType()).build(recipe, lootTracker, recipeTracker, inspectedTables, inspectedRecipes);
        if(element != null) {
            root.addFrom(element, TrackingConnection.CRAFTING);
        }
    }

    private static GraphElement addIngredients(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes, CraftingStationGraphElement root) {
        List<Ingredient> ingredients = recipeEntry.value().placementInfo().ingredients();
        List<Item> items = ingredients.stream()
                .flatMap(Ingredient::items)
                .map(Holder::value)
                .distinct()
                .toList();
        for(Item item : items) {
            GraphElement ingredient = new ItemElement(item);
            boolean known = false;
            for(LootTableDrops source : lootTracker.getSources(item)) {
                known = true;
                root.addFrom(ingredient, TrackingConnection.INGREDIENT);
                DropTypeGraphBuilders.addSource(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, source, ingredient);
            }
            for (RecipeHolder<?> recipe : recipeTracker.getRecipesFor(item)) {
                known = true;
                root.addFrom(ingredient, TrackingConnection.INGREDIENT);
                addDirect(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, recipe, ingredient);
            }
            if(!known) {
                LocalPlayer player = Minecraft.getInstance().player;
                StatsCounter statsCounter = player.getStats();
                boolean hadItem = statsCounter.getValue(Stats.ITEM_PICKED_UP, item) > 0;
                hadItem |= statsCounter.getValue(Stats.ITEM_CRAFTED, item) > 0;
                hadItem |= statsCounter.getValue(Stats.ITEM_USED, item) > 0;
                if(hadItem) {
                    root.addFrom(ingredient, TrackingConnection.INGREDIENT);
                }
            }
        }
        return root;
    }
    private static GraphElement stonecuttingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new StonecutterGraphElement(recipeEntry));
    }

    private static GraphElement smithingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new SmithingGraphElement(recipeEntry));
    }

    private static GraphElement smeltingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new FurnaceGraphElement(recipeEntry, Items.FURNACE));
    }

    private static GraphElement blastingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new FurnaceGraphElement(recipeEntry, Items.BLAST_FURNACE));
    }

    private static GraphElement campfireCookingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new FurnaceGraphElement(recipeEntry, Items.CAMPFIRE));
    }

    private static GraphElement smokingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new FurnaceGraphElement(recipeEntry, Items.SMOKER));
    }

    private static GraphElement craftingGraphBuilder(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return addIngredients(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes, new CraftingTableGraphElement(recipeEntry));
    }

    private interface ForwardingRecipeTypeGraphBuilder extends GraphBuilder<RecipeHolder<?>> {
        static @NotNull ForwardingRecipeTypeGraphBuilder forward(GraphBuilder<RecipeHolder<?>> builder) {
            return (recipe, inspectedRecipes) -> {
                if(!inspectedRecipes.add(recipe.id())) return null;

                return builder;
            };
        }

        GraphBuilder<RecipeHolder<?>> get(RecipeHolder<?> recipeEntry, Set<ResourceKey<Recipe<?>>> inspectedRecipes);

        @Override
        default GraphElement build(RecipeHolder<?> recipeEntry, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
            GraphBuilder<RecipeHolder<?>> builder = get(recipeEntry, inspectedRecipes);
            if (builder == null) return null;

            return builder.build(recipeEntry, lootTracker, recipeTracker, inspectedTables, inspectedRecipes);
        }
    }
}
