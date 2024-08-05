package com.bawnorton.trulyrandom.client.loot;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.loot.graph.GraphElement;
import com.bawnorton.trulyrandom.client.loot.graph.ItemElement;
import com.bawnorton.trulyrandom.graph.Graph;
import com.bawnorton.trulyrandom.tracker.loot.LootTableDrops;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import java.util.Set;

public class LootBookController {
    private boolean lootBookOpen;
    private Item graphItem;

    public Graph<GraphElement> createGraph(Item item) {
        ItemElement root = createElementTree(item);
        Graph<GraphElement> graph = new Graph<>();
        root.supplyGraph(graph);
        return graph;
    }

    private ItemElement createElementTree(Item item) {
        LootTableTracker tracker = TrulyRandomClient.getRandomiser().getLootTableTracker();
        ItemElement root = new ItemElement(item);
        Set<LootTableDrops> sources = tracker.getSources(item);
        for(LootTableDrops source : sources) {
            RegistryKey<LootTable> sourceKey = source.getKey();
            tracker.getFrom(sourceKey).ifPresent(from -> {
                LootTableDrops drops = tracker.getDrops(from);
                for (Item sourceItem : drops.getDrops()) {
                    ItemElement sourceElement = createElementTree(sourceItem);
                    sourceElement.addTo(root);
                }
            });
        }
        return root;
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
