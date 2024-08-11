package com.bawnorton.trulyrandom.client.loot;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.loot.graph.LootGraph;
import com.bawnorton.trulyrandom.client.loot.graph.droptype.DropTypeGraphBuilders;
import com.bawnorton.trulyrandom.client.loot.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.loot.graph.element.ItemElement;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.item.Item;
import java.util.HashSet;

public class LootBookController {
    private boolean lootBookOpen;
    private Item graphItem;
    private Item prevGraphItem;
    public int offsetX;
    public int offsetY;
    public float scale = 1;

    public LootGraph createGraph(Item item) {
        ItemElement root = createElementTree(item);
        return new LootGraph(root);
    }

    private ItemElement createElementTree(Item item) {
        LootTableTracker tracker = TrulyRandomClient.getRandomiser().getLootTableTracker();
        ItemElement root = new ItemElement(item);
        for(LootTableDrops drops : tracker.getSources(item)) {
            tracker.getFrom(drops.getKey()).ifPresent(from -> {
                LootTableDrops sourceDrops = tracker.getDrops(from);
                GraphElement element = createElementTree(sourceDrops, tracker);
                if(element != null) {
                    root.addFrom(element);
                }
            });
        }
        return root;
    }

    private GraphElement createElementTree(LootTableDrops drops, LootTableTracker tracker) {
        return DropTypeGraphBuilders.getBuilder(drops.getDropType()).build(drops, tracker, new HashSet<>());
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
