package com.bawnorton.trulyrandom.tracker.loot.drop;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.resources.Identifier;

import java.util.function.Predicate;

public record DropType(Identifier id, Predicate<LootTableIdentifier> predicate) {
    @Override
    public String toString() {
        return "DropType[id=%s]".formatted(id);
    }
}
