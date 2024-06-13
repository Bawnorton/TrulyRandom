package com.bawnorton.trulyrandom.extend;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;

public interface LookupExtender {
    LootTable trulyrandom$getUnalteredLootTable(RegistryKey<LootTable> key);
}
