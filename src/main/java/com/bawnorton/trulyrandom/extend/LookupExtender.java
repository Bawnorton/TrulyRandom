package com.bawnorton.trulyrandom.extend;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.ResourceKey;

public interface LookupExtender {
    LootTable trulyrandom$getUnalteredLootTable(ResourceKey<LootTable> key);
}
