package com.bawnorton.trulyrandom.client.loot.graph;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropType;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropTypes;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DropTypeGraphBuilders {
    private static final Map<DropType, DropTypeGraphBuilder> GRAPH_BUILDERS = new HashMap<>();

    static {
        GRAPH_BUILDERS.put(DropTypes.BLOCK, (tableDrops, tracker, inspectedTables) -> {
            LootTableIdentifier tableId = getTableId(tableDrops, inspectedTables);
            if (tableId == null) return null;

            Identifier sourceId = tableId.getSourceId();
            Block block = Registries.BLOCK.get(sourceId);
            Item item = block.asItem();
            ItemElement root = new ItemElement(item);
            for (LootTableDrops source : tracker.getSources(item)) {
                tracker.getFrom(source.getKey()).ifPresent(from -> {
                    LootTableDrops sourceDrops = tracker.getDrops(from);
                    GraphElement element = getBuilder(sourceDrops.getDropType()).build(sourceDrops, tracker, inspectedTables);
                    if (element != null) {
                        root.addFrom(element);
                    }
                });
            }
            return root;
        });
        GRAPH_BUILDERS.put(DropTypes.ENTITY, (tableDrops, tracker, inspectedTables) -> {
            LootTableIdentifier tableId = getTableId(tableDrops, inspectedTables);
            if (tableId == null) return null;

            Identifier sourceId = tableId.getSourceId();
            EntityType<?> entity = Registries.ENTITY_TYPE.get(sourceId);
            return new EntityGraphElement(entity);
        });
    }

    private static @Nullable LootTableIdentifier getTableId(LootTableDrops tableDrops, Set<RegistryKey<LootTable>> inspectedTables) {
        if(!inspectedTables.add(tableDrops.getKey())) {
            return null;
        }
        return tableDrops.getLootTableId();
    }

    public static DropTypeGraphBuilder getBuilder(DropType dropType) {
        return GRAPH_BUILDERS.getOrDefault(dropType, (tableDrops, tracker, inspectedTables) -> null);
    }
}
