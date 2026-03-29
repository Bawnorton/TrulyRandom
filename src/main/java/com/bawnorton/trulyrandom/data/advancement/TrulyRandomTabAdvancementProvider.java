package com.bawnorton.trulyrandom.data.advancement;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.data.advancement.criterion.CraftingCriterion;
import com.bawnorton.trulyrandom.data.advancement.criterion.ModuleEnabledCriterion;
import com.bawnorton.trulyrandom.random.module.Module;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class TrulyRandomTabAdvancementProvider extends FabricAdvancementProvider {
    public TrulyRandomTabAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider lookup, Consumer<AdvancementHolder> exporter) {
        HolderLookup.RegistryLookup<Item> itemRegistry = lookup.lookupOrThrow(Registries.ITEM);
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.DIAMOND,
                        Component.translatable("advancements.trulyrandom.root.title"),
                        Component.translatable("advancements.trulyrandom.root.description"),
                        Identifier.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .save(exporter, TrulyRandom.sid("root"));
        createChallengeEntry(
                root,
                Items.BOW,
                "wob",
                Map.of(
                        "wob",
                        CraftingCriterion.Conditions.create(
                                List.of(
                                        "AB ",
                                        "A B",
                                        "AB "
                                ),
                                Map.of(
                                        'A', Ingredient.of(Items.STICK),
                                        'B', Ingredient.of(Items.STRING)
                                )
                        )
                ),
                exporter
        );
        createChallengeEntry(
                root,
                Items.RED_BED,
                "deb",
                Map.of(
                        "deb",
                        CraftingCriterion.Conditions.create(
                        List.of(
                                "AAA",
                                "BBB"
                        ),
                        Map.of(
                                'A', Ingredient.of(itemRegistry.getOrThrow(ItemTags.PLANKS)),
                                'B', Ingredient.of(itemRegistry.getOrThrow(ItemTags.WOOL))
                        )
                    )
                ),
                exporter
        );
        createTaskEntry(
                root,
                Items.BREEZE_ROD,
                "this_is_not_the_rod_you_are_looking_for",
                Map.of(
                        "rod",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.BREEZE_ROD),
                        "is_loot_tables",
                        ModuleEnabledCriterion.Conditions.create(Module.LOOT_TABLES)
                ),
                exporter
        );
        createChallengeEntry(
                root,
                Items.STICK,
                "avatar",
                Map.of(
                        "wood",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK),
                        "bamboo",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.BAMBOO),
                        "breeze",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.BREEZE_ROD),
                        "blaze",
                        InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLAZE_ROD)
                ),
                exporter
        );
        createChallengeEntry(
                root,
                Items.CANDLE,
                "i_didnt_wax_for_this",
                Stream.of(
                        Items.CANDLE,
                        Items.WHITE_CANDLE,
                        Items.ORANGE_CANDLE,
                        Items.MAGENTA_CANDLE,
                        Items.LIGHT_BLUE_CANDLE,
                        Items.YELLOW_CANDLE,
                        Items.LIME_CANDLE,
                        Items.PINK_CANDLE,
                        Items.GRAY_CANDLE,
                        Items.LIGHT_GRAY_CANDLE,
                        Items.CYAN_CANDLE,
                        Items.PURPLE_CANDLE,
                        Items.BLUE_CANDLE,
                        Items.BROWN_CANDLE,
                        Items.GREEN_CANDLE,
                        Items.RED_CANDLE,
                        Items.BLACK_CANDLE
                ).collect(HashMap::new, (map, item) -> map.put(BuiltInRegistries.ITEM.getKey(item).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item)), HashMap::putAll),
                exporter
        );
        createChallengeEntry(
                root,
                Items.WHITE_WOOL,
                "woold_you_look_at_that",
                Stream.of(
                        Items.WHITE_WOOL,
                        Items.ORANGE_WOOL,
                        Items.MAGENTA_WOOL,
                        Items.LIGHT_BLUE_WOOL,
                        Items.YELLOW_WOOL,
                        Items.LIME_WOOL,
                        Items.PINK_WOOL,
                        Items.GRAY_WOOL,
                        Items.LIGHT_GRAY_WOOL,
                        Items.CYAN_WOOL,
                        Items.PURPLE_WOOL,
                        Items.BLUE_WOOL,
                        Items.BROWN_WOOL,
                        Items.GREEN_WOOL,
                        Items.RED_WOOL,
                        Items.BLACK_WOOL
                ).collect(HashMap::new, (map, item) -> map.put(BuiltInRegistries.ITEM.getKey(item).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item)), HashMap::putAll),
                exporter
        );
    }

    private AdvancementHolder createChallengeEntry(AdvancementHolder parent, Item displayItem, String name, Map<String, Criterion<?>> criterion, Consumer<AdvancementHolder> exporter) {
        return createEntry(parent, displayItem, name, AdvancementType.CHALLENGE, true, true, false, 100, criterion, exporter);
    }

    private AdvancementHolder createTaskEntry(AdvancementHolder parent, Item displayItem, String name, Map<String, Criterion<?>> criterion, Consumer<AdvancementHolder> exporter) {
        return createEntry(parent, displayItem, name, AdvancementType.TASK, true, true, false, 0, criterion, exporter);
    }

    private AdvancementHolder createEntry(AdvancementHolder parent, Item displayItem, String name, AdvancementType frame, boolean toast, boolean chat, boolean hidden, int xp, Map<String, Criterion<?>> criterion, Consumer<AdvancementHolder> exporter) {
        Advancement.Builder builder = Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        displayItem,
                        Component.translatable("advancements.trulyrandom.%s.title".formatted(name)),
                        Component.translatable("advancements.trulyrandom.%s.description".formatted(name)),
                        null,
                        frame,
                        toast,
                        chat,
                        hidden
                )
                .rewards(AdvancementRewards.Builder.experience(xp));
        criterion.forEach(builder::addCriterion);
        return builder.save(exporter, TrulyRandom.id(name).toString());
    }
}
