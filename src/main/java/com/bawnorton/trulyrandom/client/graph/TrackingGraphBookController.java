package com.bawnorton.trulyrandom.client.graph;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.droptype.DropTypeGraphBuilders;
import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.graph.element.ItemElement;
import com.bawnorton.trulyrandom.client.graph.graphbuilder.recipetype.RecipeTypeGraphBuilders;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.TrackingConnection;
import com.bawnorton.trulyrandom.tracker.loot.drop.GraphTypes;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeHolder;

public class TrackingGraphBookController {
    private boolean lootBookOpen;
    private Item graphItem;
    private Item prevGraphItem;
    public int offsetX;
    public int offsetY;
    public float scale = 1;

    public TrackingGraph createGraph(Item item) {
        ItemElement root = createElementTree(item);
        return new TrackingGraph(root);
    }

    private ItemElement createElementTree(Item item) {
        LootTableTracker lootTracker = TrulyRandomClient.getRandomiser().getLootTableTracker();
        RecipeTracker recipeTracker = TrulyRandomClient.getRandomiser().getRecipeTracker();
        ItemElement root = new ItemElement(item);
        for(LootTableDrops drops : lootTracker.getSources(item)) {
            lootTracker.getFrom(drops.getKey()).ifPresent(from -> {
                LootTableDrops sourceDrops = lootTracker.getDrops(from);
                GraphElement element = createElementTree(sourceDrops, lootTracker, recipeTracker);
                TrackingConnection connection = GraphTypes.getConnection(sourceDrops.getDropType());
                if(element != null) {
                    root.addFrom(element, connection);
                }
            });
        }
        for(RecipeHolder<?> recipe : recipeTracker.getRecipesFor(item)) {
            GraphElement element = createElementTree(recipe, lootTracker, recipeTracker);
            if(element != null) {
                root.addFrom(element, TrackingConnection.CRAFTING);
            }
        }
        return root;
    }

    private GraphElement createElementTree(LootTableDrops drops, LootTableTracker lootTracker, RecipeTracker recipeTracker) {
        return DropTypeGraphBuilders.getBuilder(drops.getDropType()).build(drops, lootTracker, recipeTracker);
    }

    private GraphElement createElementTree(RecipeHolder<?> recipe, LootTableTracker lootTracker, RecipeTracker recipeTracker) {
        return RecipeTypeGraphBuilders.getBuilder(recipe.value().getType()).build(recipe, lootTracker, recipeTracker);
    }

    public boolean isNewGraphItem() {
        if(graphItem == null) {
            return false;
        }
        return prevGraphItem != graphItem;
    }

    public boolean isLootBookOpen() {
        return lootBookOpen;
    }

    public void setLootBookOpen(boolean lootBookOpen) {
        this.lootBookOpen = lootBookOpen;
    }

    public Item getGraphItem() {
        return graphItem;
    }

    public void setGraphItem(Item graphItem) {
        this.prevGraphItem = this.graphItem;
        this.graphItem = graphItem;
    }

    public void reset() {
        this.graphItem = null;
        this.prevGraphItem = null;
        this.lootBookOpen = false;
        this.offsetX = 0;
        this.offsetY = 0;
    }
}
