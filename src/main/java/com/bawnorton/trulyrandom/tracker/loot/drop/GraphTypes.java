package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.google.common.base.Predicates;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class GraphTypes {
    private static final Map<DropType, TrackingConnection> DROP_TYPES = new HashMap<>();

    public static final DropType ARCHAELOGY = of("archaelogy", LootTableIdentifier::isFromArchaelogy, TrackingConnection.BRUSHING);
    public static final DropType BLOCK = of("block", LootTableIdentifier::isFromBlock, TrackingConnection.MINING);
    public static final DropType CHEST = of("chest", LootTableIdentifier::isFromChest, TrackingConnection.LOOTING);
    public static final DropType DISPENSER = of("dispenser", LootTableIdentifier::isFromDispenser, TrackingConnection.LOOTING);
    public static final DropType ENTITY = of("entity", LootTableIdentifier::isFromEntity, TrackingConnection.KILLING);
    public static final DropType EQUIPMENT = of("equipment", LootTableIdentifier::isFromEquipment, TrackingConnection.NONE);
    public static final DropType GAMEPLAY = of("gameplay", LootTableIdentifier::isFromGameplay, TrackingConnection.GAMEPLAY);
    public static final DropType POT = of("pot", LootTableIdentifier::isFromPot, TrackingConnection.SMASHING);
    public static final DropType SHEARING = of("shearing", LootTableIdentifier::isFromShearing, TrackingConnection.SHEARING);
    public static final DropType SPAWNER = of("spawner", LootTableIdentifier::isFromSpawner, TrackingConnection.KILLING);
    public static final DropType EMPTY = of("empty", LootTableIdentifier::isEmpty, TrackingConnection.NONE);

    public static final DropType UNKNOWN = new DropType(TrulyRandom.id("unknown"), Predicates.alwaysTrue());

    private static DropType of(String id, Predicate<LootTableIdentifier> predicate, TrackingConnection connection) {
        DropType type = new DropType(TrulyRandom.id(id), predicate);
        DROP_TYPES.put(type, connection);
        return type;
    }

    public static DropType getDropType(LootTableIdentifier identifier) {
        for (DropType dropType : DROP_TYPES.keySet()) {
            if(dropType.predicate().test(identifier)) {
                return dropType;
            }
        }
        return UNKNOWN;
    }

    public static TrackingConnection getConnection(DropType type) {
        return DROP_TYPES.getOrDefault(type, TrackingConnection.NONE);
    }
}
