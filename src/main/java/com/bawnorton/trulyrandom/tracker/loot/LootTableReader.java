package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.CompositeEntryBaseAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.DynamicLootAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.EnchantmentsPredicateAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.ItemEntryAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.LootTableAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.NestedLootTableAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.TagEntryAccessor;
import com.bawnorton.trulyrandom.tracker.loot.drop.SilkQuery;
import com.mojang.datafixers.util.Either;
import net.minecraft.advancements.criterion.EnchantmentPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class LootTableReader {
    public static List<Item> read(Registry<LootTable> lootTableRegistry, LootTable lootTable) {
        LootTableAccessor accessor = (LootTableAccessor) lootTable;
        List<LootPool> pools = accessor.trulyrandom$pools();
        List<Item> items = new ArrayList<>();
        for(LootPool pool : pools) {
            List<LootPoolEntryContainer> entries = pool.entries;
            for(LootPoolEntryContainer entry : entries) {
                items.addAll(readEntry(lootTableRegistry, entry));
            }
        }
        return items;
    }

    private static List<Item> readEntry(Registry<LootTable> lootTableRegistry, LootPoolEntryContainer entry) {
        return switch (entry) {
            case CompositeEntryBase compositeEntryBase -> {
                CompositeEntryBaseAccessor accessor = (CompositeEntryBaseAccessor) compositeEntryBase;
                List<Item> items = new ArrayList<>();
                for(LootPoolEntryContainer lootPoolEntry : accessor.trulyrandom$children()) {
                    items.addAll(readEntry(lootTableRegistry, lootPoolEntry));
                }
                yield items;
            }
            case LootPoolSingletonContainer singletonContainer -> switch (singletonContainer) {
                case DynamicLootAccessor dynamicEntry -> {
                    Identifier name = dynamicEntry.trulyrandom$name();
                    if(name.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
                        yield List.of(Items.DECORATED_POT);
                    } else if (name.equals(ShulkerBoxBlock.CONTENTS)) {
                        yield List.of(Items.SHULKER_BOX);
                    }
                    yield List.of();
                }
                case EmptyLootItem ignored -> List.of();
                case ItemEntryAccessor itemEntry -> List.of(itemEntry.trulyrandom$item().value());
                case NestedLootTableAccessor lootTableEntry -> {
                    Either<ResourceKey<LootTable>, LootTable> contents = lootTableEntry.trulyrandom$contents();
                    LootTable table = contents.map(lootTableRegistry::getValueOrThrow, Function.identity());
                    yield read(lootTableRegistry, table);
                }
                case TagEntryAccessor tagEntry -> {
                    TagKey<Item> name = tagEntry.trulyrandom$tag();
                    List<Item> items = new ArrayList<>();
                    BuiltInRegistries.ITEM.getTagOrEmpty(name).forEach(regEntry -> items.add(regEntry.value()));
                    yield items;
                }
                default -> throw new IllegalStateException("Unexpected value: " + singletonContainer);
            };
            default -> throw new IllegalStateException("Unexpected value: " + entry);
        };
    }

    public static SilkQuery queryForSilk(HolderGetter<LootTable> lootTableRegistry, LootTable table) {
        SilkQuery query = new SilkQuery();
        for(LootPool pool : ((LootTableAccessor) table).trulyrandom$pools()) {
            List<LootItemCondition> poolConditions = pool.conditions;
            boolean poolNeedsSilk = doesAnyConditionNeedSilk(poolConditions);
            for(LootPoolEntryContainer entry : pool.entries) {
                if(poolNeedsSilk) {
                    if(!(entry instanceof ItemEntryAccessor itemEntry)) {
                        TrulyRandom.LOGGER.warn("Non item entry: {}", entry.getClass().getSimpleName());
                        continue;
                    }

                    query.addNeedsSilk(itemEntry.trulyrandom$item());
                } else {
                    query.add(queryForSilk(lootTableRegistry, entry));
                }
            }
        }
        return query;
    }

    public static SilkQuery queryForSilk(HolderGetter<LootTable> lootTableRegistry, LootPoolEntryContainer poolEntry) {
        SilkQuery query = new SilkQuery();
        switch (poolEntry) {
            case CompositeEntryBaseAccessor combinedEntry -> {
                List<LootPoolEntryContainer> childEntries = combinedEntry.trulyrandom$children();
                for(LootPoolEntryContainer childEntry : childEntries) {
                    query.add(queryForSilk(lootTableRegistry, childEntry));
                }
            }
            case LootPoolSingletonContainer leafEntry -> {
                switch (leafEntry) {
                    case DynamicLoot ignored -> {}
                    case EmptyLootItem ignored -> {}
                    case TagEntry ignored -> {}
                    case ItemEntryAccessor itemEntry -> {
                        List<LootItemCondition> conditions = itemEntry.trulyrandom$conditions();
                        boolean anyConditionRequiresSilk = doesAnyConditionNeedSilk(conditions);
                        if(anyConditionRequiresSilk) {
                            query.addNeedsSilk(itemEntry.trulyrandom$item());
                        } else {
                            query.addDoesNotNeedSilk(itemEntry.trulyrandom$item());
                        }
                    }
                    case NestedLootTableAccessor lootTableEntry -> {
                        Either<ResourceKey<LootTable>, LootTable> value = lootTableEntry.trulyrandom$contents();
                        LootTable table = value.map(key -> lootTableRegistry.getOrThrow(key).value(), Function.identity());
                        query.add(queryForSilk(lootTableRegistry, table));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + leafEntry);
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + poolEntry);
        }
        return query;
    }

    private static boolean doesAnyConditionNeedSilk(List<LootItemCondition> conditions) {
        boolean anyConditionRequiresSilk = false;
        for(LootItemCondition condition : conditions) {
            if (!(condition instanceof MatchTool matchTool)) continue;

            if(doesConditionNeedSilk(matchTool)) {
                anyConditionRequiresSilk = true;
            }
        }
        return anyConditionRequiresSilk;
    }

    public static boolean doesConditionNeedSilk(MatchTool matchTool) {
        ItemPredicate predicate = matchTool.predicate().orElse(null);
        if (predicate == null) return false;

        Map<DataComponentPredicate.Type<?>, DataComponentPredicate> partial = predicate.components().partial();
        DataComponentPredicate componentPredicate = partial.get(DataComponentPredicates.ENCHANTMENTS);
        if (!(componentPredicate instanceof EnchantmentsPredicateAccessor enchantmentsPredicate)) return false;

        List<EnchantmentPredicate> enchantmentPredicates = enchantmentsPredicate.trulyrandom$enchantments();
        for (EnchantmentPredicate enchantmentPredicate : enchantmentPredicates) {
            HolderSet<Enchantment> enchantments = enchantmentPredicate.enchantments().orElse(null);
            if (enchantments == null) continue;

            for (Holder<Enchantment> enchantment : enchantments) {
                if (!enchantment.getRegisteredName().equals(Enchantments.SILK_TOUCH.identifier().toString())) continue;

                return true;
            }
        }
        return false;
    }
}
