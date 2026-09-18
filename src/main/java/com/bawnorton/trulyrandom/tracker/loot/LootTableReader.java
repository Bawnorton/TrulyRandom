package com.bawnorton.trulyrandom.tracker.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.*;
import com.bawnorton.trulyrandom.tracker.loot.drop.SilkQuery;
import com.mojang.datafixers.util.Either;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

//~ if <=26.1.2 'predicates' -> 'criterion' {
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
//~}

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
            case CompositeEntryBaseAccessor compositeEntry -> {
                List<Item> items = new ArrayList<>();
                for(LootPoolEntryContainer lootPoolEntry : compositeEntry.trulyrandom$children()) {
                    items.addAll(readEntry(lootTableRegistry, lootPoolEntry));
                }
                yield items;
            }
            case NestedLootTableAccessor lootTableEntry -> {
                //? if <=26.1.2 {
                /*Either<ResourceKey<LootTable>, LootTable> contents = lootTableEntry.trulyrandom$contents();
                LootTable table = contents.map(lootTableRegistry::getValueOrThrow, Function.identity());
                yield read(lootTableRegistry, table);
                *///?} else {
                HolderSet<LootTable> contents = lootTableEntry.trulyrandom$value();
                List<Item> items = new ArrayList<>();
                contents.stream().map(Holder::value).forEach(table -> items.addAll(read(lootTableRegistry, table)));
                yield items;
                //?}
            }
            case TagEntryAccessor tagEntry -> {
                //? if <=26.1.2 {
                /*TagKey<Item> name = tagEntry.trulyrandom$tag();
                List<Item> items = new ArrayList<>();
                BuiltInRegistries.ITEM.getTagOrEmpty(name).forEach(regEntry -> items.add(regEntry.value()));
                yield items;
                *///?} else {
                HolderSet<Item> contents = tagEntry.trulyrandom$tag();
                yield contents.stream().map(Holder::value).toList();
                //?}
            }
            case DynamicLootAccessor dynamicEntry -> {
                Identifier name = dynamicEntry.trulyrandom$name();
                if(name.equals(DecoratedPotBlock.SHERDS_DYNAMIC_DROP_ID)) {
                    yield List.of(Items.DECORATED_POT);
                } else if (name.equals(ShulkerBoxBlock.CONTENTS)) {
                    yield List.of(Items.SHULKER_BOX);
                }
                yield List.of();
            }
            case EmptyLootItem _, SlotLoot _ -> List.of();
            case LootItemAccessor itemEntry -> List.of(itemEntry.trulyrandom$item().value());
            default -> throw new IllegalStateException("Unexpected value: " + entry);
        };
    }

    public static SilkQuery queryForSilk(HolderGetter<LootTable> lootTableRegistry, LootTable table) {
        SilkQuery query = new SilkQuery();
        for(LootPool pool : ((LootTableAccessor) table).trulyrandom$pools()) {
            //? if <=26.1.2 {
            /*List<LootItemCondition> poolConditions = pool.conditions;
            boolean poolNeedsSilk = poolConditions.stream().anyMatch(LootTableReader::doesConditionNeedSilk);
            *///?} else {
            Optional<Holder<LootItemCondition>> poolCondition = pool.condition;
            boolean poolNeedsSilk = poolCondition.map(holder -> doesConditionNeedSilk(holder.value())).orElse(false);
            //?}
            for(LootPoolEntryContainer entry : pool.entries) {
                if(poolNeedsSilk) {
                    if(!(entry instanceof LootItemAccessor itemEntry)) {
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
            case DynamicLoot _, EmptyLootItem _, TagEntry _, SlotLoot _ -> {}
            case LootItemAccessor itemEntry -> {
                //? if <=26.1.2 {
                /*List<LootItemCondition> conditions = itemEntry.trulyrandom$conditions();
                boolean anyConditionRequiresSilk = conditions.stream().anyMatch(LootTableReader::doesConditionNeedSilk);
                *///?} else {
                Optional<Holder<LootItemCondition>> condition = itemEntry.trulyrandom$condition();
                boolean anyConditionRequiresSilk = condition.map(holder -> doesConditionNeedSilk(holder.value())).orElse(false);
                //?}
                if(anyConditionRequiresSilk) {
                    query.addNeedsSilk(itemEntry.trulyrandom$item());
                } else {
                    query.addDoesNotNeedSilk(itemEntry.trulyrandom$item());
                }
            }
            case NestedLootTableAccessor lootTableEntry -> {
                //? if <=26.1.2 {
                /*Either<ResourceKey<LootTable>, LootTable> value = lootTableEntry.trulyrandom$contents();
                LootTable table = value.map(key -> lootTableRegistry.getOrThrow(key).value(), Function.identity());
                query.add(queryForSilk(lootTableRegistry, table));
                *///?} else {
                HolderSet<LootTable> contents = lootTableEntry.trulyrandom$value();
                contents.stream().map(Holder::value).forEach(table -> query.add(queryForSilk(lootTableRegistry, table)));
                //?}
            }
            default -> throw new IllegalStateException("Unexpected value: " + poolEntry);
        }
        return query;
    }

    private static boolean doesConditionNeedSilk(LootItemCondition condition) {
        if (!(condition instanceof MatchTool matchTool)) return false;

        return doesMatchToolNeedSilk(matchTool);
    }

    public static boolean doesMatchToolNeedSilk(MatchTool matchTool) {
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
