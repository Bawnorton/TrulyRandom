package com.bawnorton.trulyrandom.tracker.loot.drop;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import java.util.HashSet;
import java.util.Set;

public class SilkQuery {
    private final Set<RegistryEntry<Item>> needsSilk = new HashSet<>();
    private final Set<RegistryEntry<Item>> doesNotNeedSilk = new HashSet<>();

    public void addNeedsSilk(RegistryEntry<Item> item) {
        needsSilk.add(item);
    }

    public void addDoesNotNeedSilk(RegistryEntry<Item> item) {
        doesNotNeedSilk.add(item);
    }

    public boolean hasAnyThatNeedSilk() {
        return !needsSilk.isEmpty();
    }

    public Set<RegistryEntry<Item>> getNeedsSilk() {
        return needsSilk;
    }

    public Set<RegistryEntry<Item>> getDoesNotNeedSilk() {
        return doesNotNeedSilk;
    }

    public void add(SilkQuery subQuery) {
        needsSilk.addAll(subQuery.getNeedsSilk());
        doesNotNeedSilk.addAll(subQuery.getDoesNotNeedSilk());
    }

    public boolean needsSilk(Item item) {
        return needsSilk.contains(Registries.ITEM.getEntry(item)) && !doesNotNeedSilk(item);
    }

    public boolean doesNotNeedSilk(Item item) {
        return doesNotNeedSilk.contains(Registries.ITEM.getEntry(item));
    }
}
