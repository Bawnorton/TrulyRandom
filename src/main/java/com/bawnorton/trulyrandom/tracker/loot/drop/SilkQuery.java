package com.bawnorton.trulyrandom.tracker.loot.drop;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public class SilkQuery {
    private final Set<String> needsSilk = new HashSet<>();
    private final Set<String> doesNotNeedSilk = new HashSet<>();

    public void addNeedsSilk(Holder<Item> item) {
        needsSilk.add(item.getRegisteredName());
    }

    public void addDoesNotNeedSilk(Holder<Item> item) {
        doesNotNeedSilk.add(item.getRegisteredName());
    }

    public boolean hasAnyThatNeedSilk() {
        return !needsSilk.isEmpty();
    }

    public Set<String> getNeedsSilk() {
        return needsSilk;
    }

    public Set<String> getDoesNotNeedSilk() {
        return doesNotNeedSilk;
    }

    public void add(SilkQuery subQuery) {
        needsSilk.addAll(subQuery.getNeedsSilk());
        doesNotNeedSilk.addAll(subQuery.getDoesNotNeedSilk());
    }

    public boolean needsSilk(Item item) {
        return needsSilk.contains(BuiltInRegistries.ITEM.getKey(item).toString()) && !doesNotNeedSilk(item);
    }

    public boolean doesNotNeedSilk(Item item) {
        return doesNotNeedSilk.contains(BuiltInRegistries.ITEM.getKey(item).toString());
    }
}
