package com.bawnorton.trulyrandom.client.graph.graphbuilder.droptype;

import com.bawnorton.trulyrandom.client.graph.element.*;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.GraphBuilder;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.recipetype.RecipeTypeGraphBuilders;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropType;
import com.bawnorton.trulyrandom.tracker.loot.drop.TrackingConnection;
import com.bawnorton.trulyrandom.tracker.loot.drop.GraphTypes;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeHolder;
import net.minecraft.registry.BuiltInRegistries;
import net.minecraft.registry.ResourceKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DropTypeGraphBuilders {
    private static final Map<DropType, GraphBuilder<LootTableDrops>> GRAPH_BUILDERS = new HashMap<>() {
        @Override
        public GraphBuilder<LootTableDrops> put(DropType key, GraphBuilder<LootTableDrops> value) {
            return super.put(key, ForwardingDropTypeGraphBuilder.forward(value));
        }
    };

    static {
        GRAPH_BUILDERS.put(GraphTypes.ARCHAELOGY, DropTypeGraphBuilders::archaelogyGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.BLOCK, DropTypeGraphBuilders::blockGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.CHEST, DropTypeGraphBuilders::chestGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.DISPENSER, DropTypeGraphBuilders::dispenserGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.ENTITY, DropTypeGraphBuilders::entityGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.EQUIPMENT, (tableDrops, lootTracker, recipeTracker, inspectedTables, inspectedRecipes) -> null);
        GRAPH_BUILDERS.put(GraphTypes.GAMEPLAY, DropTypeGraphBuilders::gameplayGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.POT, DropTypeGraphBuilders::potGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.SHEARING, DropTypeGraphBuilders::shearingGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.SPAWNER, DropTypeGraphBuilders::spawnerGraphBuilder);
        GRAPH_BUILDERS.put(GraphTypes.EMPTY, (tableDrops, lootTracker, recipeTracker, inspectedTables, inspectedRecipes) -> null);
        GRAPH_BUILDERS.put(GraphTypes.UNKNOWN, (tableDrops, lootTracker, recipeTracker, inspectedTables, inspectedRecipes) -> null);
    }

    public static GraphBuilder<LootTableDrops> getBuilder(DropType dropType) {
        return GRAPH_BUILDERS.getOrDefault(dropType, (ForwardingDropTypeGraphBuilder) (tableDrops, inspectedTables) -> null);
    }
    
    private static GraphElement archaelogyGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return new ItemElement(Items.BRUSH);
    }

    private static GraphElement blockGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        Block block = BuiltInRegistries.BLOCK.get(sourceId);
        Item item = block.asItem();
        if(item == Items.AIR) {
            return literalBlockGraphBuilder(tableDrops, lootTracker, recipeTracker, inspectedTables, inspectedRecipes);
        }
        ItemElement root = new ItemElement(item);
        for (LootTableDrops source : lootTracker.getSources(item)) {
            addSource(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, source, root);
        }
        for (RecipeHolder<?> recipe : recipeTracker.getRecipesFor(item)) {
            RecipeTypeGraphBuilders.addDirect(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, recipe, root);
        }
        return root;
    }

    private static GraphElement literalBlockGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        Block block = BuiltInRegistries.BLOCK.get(sourceId);
        BlockElement root = new BlockElement(block);
        List<Item> parts = tableDrops.getItems();
        for(Item item : parts) {
            for (LootTableDrops source : lootTracker.getSources(item)) {
                if (!source.getItems().equals(parts)) continue;

                addSource(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, source, root);
            }
            for (RecipeHolder<?> recipe : recipeTracker.getRecipesFor(item)) {
                RecipeTypeGraphBuilders.addDirect(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, recipe, root);
            }
        }
        return root;
    }

    public static void addSource(LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes, LootTableDrops source, GraphElement root) {
        lootTracker.getFrom(source.getKey()).ifPresent(from -> {
            LootTableDrops sourceDrops = lootTracker.getDrops(from);
            addDirect(lootTracker, recipeTracker, inspectedTables, inspectedRecipes, sourceDrops, root);
        });
    }

    public static void addDirect(LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes, LootTableDrops source, GraphElement root) {
        GraphElement element = getBuilder(source.getDropType()).build(source, lootTracker, recipeTracker, inspectedTables, inspectedRecipes);
        TrackingConnection connection = GraphTypes.getConnection(source.getDropType());
        if (element != null) {
            root.addFrom(element, connection);
        }
    }

    private static GraphElement chestGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        if(tableId.isReward()) {
            return new VaultGraphElement(tableId);
        }
        return new ChestGraphElement(tableId, tableDrops.getItems());
    }

    private static GraphElement dispenserGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return new DispenserGraphElement(tableDrops.getLootTableId());
    }

    private static GraphElement entityGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.get(sourceId);
        return new EntityGraphElement(entity);
    }

    private static GraphElement gameplayGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        if(tableId.isHeroOfTheVillage()) {
            return new HeroOfTheVillagerGraphElement(tableId);
        } else {
            return new GameplayGraphElement(tableDrops.getLootTableId());
        }
    }

    private static GraphElement potGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return new ItemElement(Items.DECORATED_POT);
    }

    private static GraphElement shearingGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return new ItemElement(Items.SHEARS);
    }

    private static GraphElement spawnerGraphBuilder(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
        return new ItemElement(Items.TRIAL_SPAWNER);
    }

    private interface ForwardingDropTypeGraphBuilder extends GraphBuilder<LootTableDrops> {
        static @NotNull ForwardingDropTypeGraphBuilder forward(GraphBuilder<LootTableDrops> builder) {
            return (tableDrops, inspectedTables) -> {
                if(!inspectedTables.add(tableDrops.getKey())) return null;

                return builder;
            };
        }

        GraphBuilder<LootTableDrops> get(LootTableDrops tableDrops, Set<ResourceKey<LootTable>> inspectedTables);

        @Override
        default GraphElement build(LootTableDrops tableDrops, LootTableTracker lootTracker, RecipeTracker recipeTracker, Set<ResourceKey<LootTable>> inspectedTables, Set<ResourceKey<Recipe<?>>> inspectedRecipes) {
            GraphBuilder<LootTableDrops> builder = get(tableDrops, inspectedTables);
            if (builder == null) return null;

            return builder.build(tableDrops, lootTracker, recipeTracker, inspectedTables, inspectedRecipes);
        }
    }
}
