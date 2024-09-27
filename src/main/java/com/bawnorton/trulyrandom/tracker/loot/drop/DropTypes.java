package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.google.common.base.Predicates;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class DropTypes {
    private static final Set<DropType> DROP_TYPES = new HashSet<>();

    public static final DropType ARCHAELOGY = of("archaelogy", LootTableIdentifier::isFromArchaelogy);
    public static final DropType BLOCK = of("block", LootTableIdentifier::isFromBlock);
    public static final DropType CHEST = of("chest", LootTableIdentifier::isFromChest);
    public static final DropType DISPENSER = of("dispenser", LootTableIdentifier::isFromDispenser);
    public static final DropType ENTITY = of("entity", LootTableIdentifier::isFromEntity);
    public static final DropType EQUIPMENT = of("equipment", LootTableIdentifier::isFromEquipment);
    public static final DropType GAMEPLAY = of("gameplay", LootTableIdentifier::isFromGameplay);
    public static final DropType POT = of("pot", LootTableIdentifier::isFromPot);
    public static final DropType SHEARING = of("shearing", LootTableIdentifier::isFromShearing);
    public static final DropType SPAWNER = of("spawner", LootTableIdentifier::isFromSpawner);
    public static final DropType EMPTY = of("empty", LootTableIdentifier::isEmpty);

    public static final DropType UNKNOWN = new DropType(TrulyRandom.id("unknown"), Predicates.alwaysTrue());

    private static DropType of(String id, Predicate<LootTableIdentifier> predicate) {
        DropType type = new DropType(TrulyRandom.id(id), predicate);
        DROP_TYPES.add(type);
        return type;
    }

    public static DropType getDropType(LootTableIdentifier identifier) {
        for (DropType dropType : DROP_TYPES) {
            if(dropType.predicate().test(identifier)) {
                return dropType;
            }
        }
        return UNKNOWN;
    }
}
