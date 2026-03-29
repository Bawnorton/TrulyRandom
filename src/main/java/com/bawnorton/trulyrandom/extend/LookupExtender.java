package com.bawnorton.trulyrandom.extend;


import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public interface LookupExtender {
    LootTable trulyrandom$getUnalteredLootTable(ResourceKey<LootTable> key);
}
