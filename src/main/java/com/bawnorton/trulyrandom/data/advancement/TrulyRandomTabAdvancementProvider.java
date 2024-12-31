package com.bawnorton.trulyrandom.data.advancement;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.data.advancement.criterion.CraftingCriterion;
import com.bawnorton.trulyrandom.data.advancement.criterion.ModuleEnabledCriterion;
import com.bawnorton.trulyrandom.random.module.Module;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class TrulyRandomTabAdvancementProvider extends FabricAdvancementProvider {
    public TrulyRandomTabAdvancementProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookup) {
        super(output, lookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup lookup, Consumer<AdvancementEntry> exporter) {
        RegistryWrapper<Item> itemRegistry = lookup.getOrThrow(RegistryKeys.ITEM);
        AdvancementEntry root = Advancement.Builder.create()
                .display(
                        Items.DIAMOND,
                        Text.translatable("advancements.trulyrandom.root.title"),
                        Text.translatable("advancements.trulyrandom.root.description"),
                        Identifier.ofVanilla("textures/gui/advancements/backgrounds/adventure.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                )
                .criterion("tick", TickCriterion.Conditions.createTick())
                .build(exporter, TrulyRandom.sid("root"));
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
                                        'A', Ingredient.ofItems(Items.STICK),
                                        'B', Ingredient.ofItems(Items.STRING)
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
                                'A', Ingredient.fromTag(itemRegistry.getOrThrow(ItemTags.PLANKS)),
                                'B', Ingredient.fromTag(itemRegistry.getOrThrow(ItemTags.WOOL))
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
                        InventoryChangedCriterion.Conditions.items(Items.BREEZE_ROD),
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
                        InventoryChangedCriterion.Conditions.items(Items.STICK),
                        "bamboo",
                        InventoryChangedCriterion.Conditions.items(Items.BAMBOO),
                        "breeze",
                        InventoryChangedCriterion.Conditions.items(Items.BREEZE_ROD),
                        "blaze",
                        InventoryChangedCriterion.Conditions.items(Items.BLAZE_ROD)
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
                ).collect(HashMap::new, (map, item) -> map.put(Registries.ITEM.getId(item).getPath(), InventoryChangedCriterion.Conditions.items(item)), HashMap::putAll),
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
                ).collect(HashMap::new, (map, item) -> map.put(Registries.ITEM.getId(item).getPath(), InventoryChangedCriterion.Conditions.items(item)), HashMap::putAll),
                exporter
        );
    }

    private AdvancementEntry createChallengeEntry(AdvancementEntry parent, Item displayItem, String name, Map<String, AdvancementCriterion<?>> criterion, Consumer<AdvancementEntry> exporter) {
        return createEntry(parent, displayItem, name, AdvancementFrame.CHALLENGE, true, true, false, 100, criterion, exporter);
    }

    private AdvancementEntry createTaskEntry(AdvancementEntry parent, Item displayItem, String name, Map<String, AdvancementCriterion<?>> criterion, Consumer<AdvancementEntry> exporter) {
        return createEntry(parent, displayItem, name, AdvancementFrame.TASK, true, true, false, 0, criterion, exporter);
    }

    private AdvancementEntry createEntry(AdvancementEntry parent, Item displayItem, String name, AdvancementFrame frame, boolean toast, boolean chat, boolean hidden, int xp, Map<String, AdvancementCriterion<?>> criterion, Consumer<AdvancementEntry> exporter) {
        Advancement.Builder builder = Advancement.Builder.create()
                .parent(parent)
                .display(
                        displayItem,
                        Text.translatable("advancements.trulyrandom.%s.title".formatted(name)),
                        Text.translatable("advancements.trulyrandom.%s.description".formatted(name)),
                        null,
                        frame,
                        toast,
                        chat,
                        hidden
                )
                .rewards(AdvancementRewards.Builder.experience(xp));
        criterion.forEach(builder::criterion);
        return builder.build(exporter, TrulyRandom.id(name).toString());
    }
}
