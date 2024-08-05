package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.mixin.accessor.*;
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
import net.minecraft.predicate.item.EnchantmentsPredicate;
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
                case DynamicEntry dynamicEntry -> {
                    DynamicEntryAccessor accessor = (DynamicEntryAccessor) dynamicEntry;
                    Identifier name = accessor.getName();
                    if(name.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
                        yield List.of(Items.DECORATED_POT);
                    } else if (name.equals(ShulkerBoxBlock.CONTENTS_DYNAMIC_DROP_ID)) {
                        yield List.of(Items.SHULKER_BOX);
                    }
                    yield List.of();
                }
                case EmptyEntry ignored -> List.of();
                case ItemEntry itemEntry -> {
                    ItemEntryAccessor accessor = (ItemEntryAccessor) itemEntry;
                    yield List.of(accessor.getItem().value());
                }
                case LootTableEntry lootTableEntry -> {
                    LootTableEntryAccessor accessor = (LootTableEntryAccessor) lootTableEntry;
                    Either<RegistryKey<LootTable>, LootTable> value = accessor.getValue();
                    LootTable table = value.map(lootTableRegistry::get, Function.identity());
                    yield read(lootTableRegistry, table);
                }
                case TagEntry tagEntry -> {
                    TagEntryAccessor accessor = (TagEntryAccessor) tagEntry;
                    TagKey<Item> name = accessor.getName();
                    List<Item> items = new ArrayList<>();
                    Registries.ITEM.iterateEntries(name).forEach(regEntry -> items.add(regEntry.value()));
                    yield items;
                }
                default -> throw new IllegalStateException("Unexpected value: " + entry);
            };
            default -> throw new IllegalStateException("Unexpected value: " + entry);
        };
    }

    public static boolean determineIfNeedsSilk(LootTable table) {
        for(LootPool pool : ((LootTableAccessor) table).getPools()) {
            List<LootCondition> conditions = getConditionsFromPool(pool);

            for(LootCondition condition : conditions) {
                if (!(condition instanceof MatchToolLootCondition matchToolLootCondition)) continue;

                ItemPredicate predicate = matchToolLootCondition.predicate().orElse(null);
                if (predicate == null) continue;

                ItemSubPredicate subPredicate = predicate.subPredicates().get(ItemSubPredicateTypes.ENCHANTMENTS);
                if (!(subPredicate instanceof EnchantmentsPredicate enchantmentsPredicate)) continue;

                List<EnchantmentPredicate> enchantmentPredicates = ((EnchantmentsPredicateAccessor) enchantmentsPredicate).callGetEnchantments();
                for(EnchantmentPredicate enchantmentPredicate : enchantmentPredicates) {
                    RegistryEntryList<Enchantment> enchantments = enchantmentPredicate.enchantments().orElse(null);
                    if (enchantments == null) continue;

                    for(RegistryEntry<Enchantment> enchantment : enchantments) {
                        return enchantment.getIdAsString().equals(Enchantments.SILK_TOUCH.getValue().toString());
                    }
                }
            }
        }
        return false;
    }

    private static List<LootCondition> getConditionsFromPool(LootPool pool) {
        List<LootCondition> conditions = new ArrayList<>(pool.conditions);
        scanEntries(pool.entries, conditions);
        return conditions;
    }

    private static void scanEntries(List<LootPoolEntry> children, List<LootCondition> conditions) {
        for(LootPoolEntry entry : children) {
            conditions.addAll(((LootPoolEntryAccessor) entry).getConditions());
            if (!(entry instanceof CombinedEntry)) continue;

            List<LootPoolEntry> entries = ((CombinedEntryAccessor) entry).getChildren();
            scanEntries(entries, conditions);
        }
    }
}
