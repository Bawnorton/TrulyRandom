package com.bawnorton.trulyrandom.client.loot.graph.droptype;

import com.bawnorton.trulyrandom.client.loot.graph.element.*;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropType;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropTypes;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DropTypeGraphBuilders {
    private static final Map<DropType, DropTypeGraphBuilder> GRAPH_BUILDERS = new HashMap<>() {
        @Override
        public DropTypeGraphBuilder put(DropType key, DropTypeGraphBuilder value) {
            return super.put(key, forward(value));
        }
    };

    static {
        GRAPH_BUILDERS.put(DropTypes.ARCHAELOGY, DropTypeGraphBuilders::archaelogyGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.BLOCK, DropTypeGraphBuilders::blockGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.CHEST, DropTypeGraphBuilders::chestGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.DISPENSER, DropTypeGraphBuilders::dispenserGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.ENTITY, DropTypeGraphBuilders::entityGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.EQUIPMENT, (tableDrops, tracker, inspectedTables) -> null);
        GRAPH_BUILDERS.put(DropTypes.GAMEPLAY, DropTypeGraphBuilders::gameplayGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.POT, DropTypeGraphBuilders::potGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.SHEARING, DropTypeGraphBuilders::shearingGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.SPAWNER, DropTypeGraphBuilders::spawnerGraphBuilder);
        GRAPH_BUILDERS.put(DropTypes.EMPTY, (tableDrops, tracker, inspectedTables) -> null);
    }

    private static @NotNull ForwardingDropTypeGraphBuilder forward(DropTypeGraphBuilder builder) {
        return (tableDrops, inspectedTables) -> {
            if(!inspectedTables.add(tableDrops.getKey())) return null;

            return builder;
        };
    }

    public static DropTypeGraphBuilder getBuilder(DropType dropType) {
        return GRAPH_BUILDERS.getOrDefault(dropType, (ForwardingDropTypeGraphBuilder) (tableDrops, inspectedTables) -> null);
    }


    private static GraphElement archaelogyGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables) {
        return new ItemElement(Items.BRUSH);
    }

    private static GraphElement blockGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        Block block = Registries.BLOCK.get(sourceId);
        Item item = block.asItem();
        if(item == Items.AIR) {
            return literalBlockGraphBuilder(tableDrops, tracker, inspectedTables);
        }
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
    }

    private static GraphElement literalBlockGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        Block block = Registries.BLOCK.get(sourceId);
        BlockElement root = new BlockElement(block);
        List<Item> parts = tableDrops.getItems();
        for(Item item : parts) {
            for (LootTableDrops source : tracker.getSources(item)) {
                if (!source.getItems().equals(parts)) continue;

                tracker.getFrom(source.getKey()).ifPresent(from -> {
                    LootTableDrops sourceDrops = tracker.getDrops(from);
                    GraphElement element = getBuilder(sourceDrops.getDropType()).build(sourceDrops, tracker, inspectedTables);
                    if (element != null) {
                        root.addFrom(element);
                    }
                });
            }
        }
        return root;
    }

    private static GraphElement chestGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        return new ChestGraphElement(tableDrops.getLootTableId());
    }

    private static GraphElement dispenserGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        return new DispenserGraphElement(tableDrops.getLootTableId());
    }

    private static GraphElement entityGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        Identifier sourceId = tableId.getSourceId();
        EntityType<?> entity = Registries.ENTITY_TYPE.get(sourceId);
        return new EntityGraphElement(entity);
    }

    private static GraphElement gameplayGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        LootTableIdentifier tableId = tableDrops.getLootTableId();
        if(tableId.isHeroOfTheVillage()) {
            return new HeroOfTheVillagerGraphElement(tableId);
        } else {
            return new GameplayGraphElement(tableDrops.getLootTableId());
        }
    }

    private static GraphElement potGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        return new ItemElement(Items.DECORATED_POT);
    }

    private static GraphElement shearingGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        return new ItemElement(Items.SHEARS);
    }

    private static GraphElement spawnerGraphBuilder(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> registryKeys) {
        return new ItemElement(Items.TRIAL_SPAWNER);
    }

    private interface ForwardingDropTypeGraphBuilder extends DropTypeGraphBuilder {
        DropTypeGraphBuilder get(LootTableDrops tableDrops, Set<RegistryKey<LootTable>> inspectedTables);

        @Override
        default GraphElement build(LootTableDrops tableDrops, LootTableTracker tracker, Set<RegistryKey<LootTable>> inspectedTables) {
            DropTypeGraphBuilder builder = get(tableDrops, inspectedTables);
            if (builder == null) return null;

            return builder.build(tableDrops, tracker, inspectedTables);
        }
    }
}
