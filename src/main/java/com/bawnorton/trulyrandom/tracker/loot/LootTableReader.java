package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.*;
import com.bawnorton.trulyrandom.tracker.loot.drop.SilkQuery;
import com.mojang.datafixers.util.Either;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.entry.*;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class LootTableReader {
    public static List<Item> read(Registry<LootTable> lootTableRegistry, LootTable lootTable) {
        LootTableAccessor accessor = (LootTableAccessor) lootTable;
        List<LootPool> pools = accessor.getPools();
        List<Item> items = new ArrayList<>();
        for(LootPool pool : pools) {
            List<LootPoolEntry> entries = pool.entries;
            for(LootPoolEntry entry : entries) {
                items.addAll(readEntry(lootTableRegistry, entry));
            }
        }
        return items;
    }

    private static List<Item> readEntry(Registry<LootTable> lootTableRegistry, LootPoolEntry entry) {
        return switch (entry) {
            case CombinedEntry combinedEntry -> {
                CombinedEntryAccessor accessor = (CombinedEntryAccessor) combinedEntry;
                List<Item> items = new ArrayList<>();
                for(LootPoolEntry lootPoolEntry : accessor.getChildren()) {
                    items.addAll(readEntry(lootTableRegistry, lootPoolEntry));
                }
                yield items;
            }
            case LeafEntry leafEntry -> switch (leafEntry) {
                case DynamicEntryAccessor dynamicEntry -> {
                    Identifier name = dynamicEntry.getName();
                    if(name.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
                        yield List.of(Items.DECORATED_POT);
                    } else if (name.equals(ShulkerBoxBlock.CONTENTS_DYNAMIC_DROP_ID)) {
                        yield List.of(Items.SHULKER_BOX);
                    }
                    yield List.of();
                }
                case EmptyEntry ignored -> List.of();
                case ItemEntryAccessor itemEntry -> List.of(itemEntry.getItem().value());
                case LootTableEntryAccessor lootTableEntry -> {
                    Either<RegistryKey<LootTable>, LootTable> value = lootTableEntry.getValue();
                    LootTable table = value.map(lootTableRegistry::get, Function.identity());
                    yield read(lootTableRegistry, table);
                }
                case TagEntryAccessor tagEntry -> {
                    TagKey<Item> name = tagEntry.getName();
                    List<Item> items = new ArrayList<>();
                    Registries.ITEM.iterateEntries(name).forEach(regEntry -> items.add(regEntry.value()));
                    yield items;
                }
                default -> throw new IllegalStateException("Unexpected value: " + leafEntry);
            };
            default -> throw new IllegalStateException("Unexpected value: " + entry);
        };
    }

    public static SilkQuery queryForSilk(Registry<LootTable> lootTableRegistry, LootTable table) {
        SilkQuery query = new SilkQuery();
        for(LootPool pool : ((LootTableAccessor) table).getPools()) {
            List<LootCondition> poolConditions = pool.conditions;
            boolean poolNeedsSilk = doesAnyConditionNeedSilk(poolConditions);
            for(LootPoolEntry entry : pool.entries) {
                if(poolNeedsSilk) {
                    if(!(entry instanceof ItemEntryAccessor itemEntry)) {
                        TrulyRandom.LOGGER.warn("Non item entry: {}", entry.getClass().getSimpleName());
                        continue;
                    }

                    query.addNeedsSilk(itemEntry.getItem());
                } else {
                    query.add(queryForSilk(lootTableRegistry, entry));
                }
            }
        }
        return query;
    }

    public static SilkQuery queryForSilk(Registry<LootTable> lootTableRegistry, LootPoolEntry poolEntry) {
        SilkQuery query = new SilkQuery();
        switch (poolEntry) {
            case CombinedEntryAccessor combinedEntry -> {
                List<LootPoolEntry> childEntries = combinedEntry.getChildren();
                for(LootPoolEntry childEntry : childEntries) {
                    query.add(queryForSilk(lootTableRegistry, childEntry));
                }
            }
            case LeafEntry leafEntry -> {
                switch (leafEntry) {
                    case DynamicEntry ignored -> {}
                    case EmptyEntry ignored -> {}
                    case TagEntry ignored -> {}
                    case ItemEntryAccessor itemEntry -> {
                        List<LootCondition> conditions = itemEntry.getConditions();
                        boolean anyConditionRequiresSilk = doesAnyConditionNeedSilk(conditions);
                        if(anyConditionRequiresSilk) {
                            query.addNeedsSilk(itemEntry.getItem());
                        } else {
                            query.addDoesNotNeedSilk(itemEntry.getItem());
                        }
                    }
                    case LootTableEntryAccessor lootTableEntry -> {
                        Either<RegistryKey<LootTable>, LootTable> value = lootTableEntry.getValue();
                        LootTable table = value.map(lootTableRegistry::get, Function.identity());
                        query.add(queryForSilk(lootTableRegistry, table));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + leafEntry);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + poolEntry);
        }
        return query;
    }

    private static boolean doesAnyConditionNeedSilk(List<LootCondition> conditions) {
        boolean anyConditionRequiresSilk = false;
        for(LootCondition condition : conditions) {
            if (!(condition instanceof MatchToolLootCondition matchToolLootCondition)) continue;

            if(doesConditionNeedSilk(matchToolLootCondition)) {
                anyConditionRequiresSilk = true;
            }
        }
        return anyConditionRequiresSilk;
    }

    public static boolean doesConditionNeedSilk(MatchToolLootCondition condition) {
        ItemPredicate predicate = condition.predicate().orElse(null);
        if (predicate == null) return false;

        ItemSubPredicate subPredicate = predicate.subPredicates().get(ItemSubPredicateTypes.ENCHANTMENTS);
        if (!(subPredicate instanceof EnchantmentsPredicateAccessor enchantmentsPredicate)) return false;

        List<EnchantmentPredicate> enchantmentPredicates = enchantmentsPredicate.callGetEnchantments();
        for (EnchantmentPredicate enchantmentPredicate : enchantmentPredicates) {
            RegistryEntryList<Enchantment> enchantments = enchantmentPredicate.enchantments().orElse(null);
            if (enchantments == null) continue;

            for (RegistryEntry<Enchantment> enchantment : enchantments) {
                if (!enchantment.getIdAsString().equals(Enchantments.SILK_TOUCH.getValue().toString())) continue;

                return true;
            }
        }
        return false;
    }
}
