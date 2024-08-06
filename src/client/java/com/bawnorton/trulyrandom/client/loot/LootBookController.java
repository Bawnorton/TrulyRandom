package com.bawnorton.trulyrandom.client.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.loot.graph.DropTypeGraphBuilders;
import com.bawnorton.trulyrandom.client.loot.graph.GraphElement;
import com.bawnorton.trulyrandom.client.loot.graph.ItemElement;
import com.bawnorton.trulyrandom.graph.Graph;
import com.bawnorton.trulyrandom.graph.positioner.OrthogonalTreePositioner;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.item.Item;
import java.util.HashSet;

public class LootBookController {
    private boolean lootBookOpen;
    private Item graphItem;

    public Graph<GraphElement> createGraph(Item item) {
        ItemElement root = createElementTree(item);
        Graph<GraphElement> graph = new Graph<>();
        graph.setRoot(root.supplyGraph(graph));
        graph.position(new OrthogonalTreePositioner<>());
        return graph;
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
        TrulyRandom.LOGGER.info("tableId: {}, dropType: {}", drops.getLootTableId(), drops.getDropType());
        return DropTypeGraphBuilders.getBuilder(drops.getDropType()).build(drops, tracker, new HashSet<>());
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
        this.graphItem = graphItem;
    }
}
