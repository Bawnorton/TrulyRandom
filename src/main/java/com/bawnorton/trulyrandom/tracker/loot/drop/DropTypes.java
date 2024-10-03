package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.difficulty.DifficultyRating;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.google.common.base.Predicates;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class DropTypes {
    private static final Set<DropType> DROP_TYPES = new HashSet<>();

    public static final DropType ARCHAELOGY = of("archaelogy", LootTableIdentifier::isFromArchaelogy, DifficultyRating.EXTREME);
    public static final DropType BLOCK = of("block", LootTableIdentifier::isFromBlock, DifficultyRating.TRIVIAL);
    public static final DropType CHEST = of("chest", LootTableIdentifier::isFromChest, DifficultyRating.HARD);
    public static final DropType DISPENSER = of("dispenser", LootTableIdentifier::isFromDispenser, DifficultyRating.HARD);
    public static final DropType ENTITY = of("entity", LootTableIdentifier::isFromEntity, DifficultyRating.HARD);
    public static final DropType EQUIPMENT = of("equipment", LootTableIdentifier::isFromEquipment, DifficultyRating.EXTREME);
    public static final DropType GAMEPLAY = of("gameplay", LootTableIdentifier::isFromGameplay, DifficultyRating.HARD);
    public static final DropType POT = of("pot", LootTableIdentifier::isFromPot, DifficultyRating.EXTREME);
    public static final DropType SHEARING = of("shearing", LootTableIdentifier::isFromShearing, DifficultyRating.HARD);
    public static final DropType SPAWNER = of("spawner", LootTableIdentifier::isFromSpawner, DifficultyRating.EXTREME);
    public static final DropType EMPTY = of("empty", LootTableIdentifier::isEmpty, DifficultyRating.UNKNOWN);

    public static final DropType UNKNOWN = new DropType(TrulyRandom.id("unknown"), Predicates.alwaysTrue(), DifficultyRating.UNKNOWN);

    private static DropType of(String id, Predicate<LootTableIdentifier> predicate, DifficultyRating rating) {
        DropType type = new DropType(TrulyRandom.id(id), predicate, rating);
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
