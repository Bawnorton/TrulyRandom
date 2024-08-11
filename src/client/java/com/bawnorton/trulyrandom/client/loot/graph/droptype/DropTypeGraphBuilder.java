package com.bawnorton.trulyrandom.client.loot.graph.droptype;

import com.bawnorton.trulyrandom.client.loot.graph.element.GraphElement;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import java.util.Set;

public interface DropTypeGraphBuilder {
    GraphElement build(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables);
}
