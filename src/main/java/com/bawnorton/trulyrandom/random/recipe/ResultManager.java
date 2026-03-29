package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.bawnorton.trulyrandom.extend.ResultHolder;
import com.bawnorton.trulyrandom.mixin.accessor.SmithingTrimRecipeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.equipment.trim.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class ResultManager {
    private final Map<Class<? extends Recipe<?>>, ResultGetter> getters = new HashMap<>();
    private Random random;

    public ResultManager() {
        getters.put(SmithingTrimRecipe.class, new SmithingTrimResultGetter());
        getters.put(BookCloningRecipe.class, this::getBookCloningResult);
        getters.put(BannerDuplicateRecipe.class, this::getBannerDuplicateResult);
        getters.put(FireworkStarFadeRecipe.class, this::getFireworkStarFadeResult);
        getters.put(FireworkStarRecipe.class, this::getFireworkStarResult);
        getters.put(FireworkRocketRecipe.class, this::getFireworkRocketResult);
        getters.put(ShieldDecorationRecipe.class, this::getShieldDecorationResult);
        getters.put(ImbueRecipe.class, this::getImbueResult);
        getters.put(DyeRecipe.class, this::getDyeResult);
//        getters.put(MapCloningRecipe.class, this::getMapCloningResult);
        getters.put(MapExtendingRecipe.class, this::getMapCloningResult);
        getters.put(DecoratedPotRecipe.class, this::getDecoratedPotResult);
        getters.put(RepairItemRecipe.class, this::getRepairItemResult);
        getters.put(TransmuteRecipe.class, this::getTransmuteResult);
    }

    public void setRandom(long seed) {
        random = new Random(seed);
    }

    public ItemStack getResult(RecipeHolder<?> recipe, MinecraftServer server) {
        ItemStack result = getters.getOrDefault(recipe.value().getClass(), this::getNormalResult).getResult(recipe, server);
        if(result.isEmpty()) {
            result = Items.PAPER.getDefaultInstance();
            result.set(DataComponents.CUSTOM_NAME, Component.literal("This is here so the game doesn't crash"));
            result.set(DataComponents.LORE, new ItemLore(List.of(Component.literal("Unknown recipe type: " + recipe.id()))));
            TrulyRandom.LOGGER.warn("Unknown recipe type: {}", recipe.value().getClass().getSimpleName());
        }
        return result;
    }

    public RecipeHolder<?> setResult(RecipeHolder<?> recipe, ItemStack newResult) {
        if (recipe.value() instanceof ResultHolder resultHolder) {
            resultHolder.trulyrandom$setResult(newResult);
            return recipe;
        }
        return recipe;
    }

    public RecipeHolder<?> clearOrSetResult(RecipeHolder<?> recipe, ItemStack result) {
        if (recipe.value() instanceof ResultClearer resultClearer) {
            resultClearer.trulyrandom$clearResult();
            return recipe;
        }
        return setResult(recipe, result);
    }

    private ItemStack getNormalResult(RecipeHolder<?> recipe, MinecraftServer server) {
        if(recipe.value() instanceof ResultHolder resultHolder) {
            return resultHolder.trulyrandom$getResult();
        }
        throw new UnsupportedOperationException("Recipe type \"" + recipe.value().getClass().getSimpleName() + "\" is not supported.");
    }

    private ItemStack getBookCloningResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.WRITABLE_BOOK.getDefaultInstance();
    }

    private ItemStack getBannerDuplicateResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.WHITE_BANNER.getDefaultInstance();
    }

    private ItemStack getFireworkStarFadeResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultInstance();
    }

    private ItemStack getFireworkStarResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultInstance();
    }

    private ItemStack getFireworkRocketResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_ROCKET.getDefaultInstance();
    }

    private ItemStack getShieldDecorationResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.SHIELD.getDefaultInstance();
    }

    private ItemStack getImbueResult(RecipeHolder<?> recipe, MinecraftServer server) {
        List<Holder<Potion>> list = BuiltInRegistries.POTION
                .listElements()
                .filter(entry -> !entry.value().getEffects().isEmpty())
                .collect(Collectors.toList());
        Holder<Potion> registryEntry = list.get(random.nextInt(list.size()));
        ItemStack arrow = Items.TIPPED_ARROW.getDefaultInstance();
        arrow.setCount(8);
        arrow.set(DataComponents.POTION_CONTENTS, new PotionContents(registryEntry));
        return arrow;
    }

    private ItemStack getDyeResult(RecipeHolder<?> recipe, MinecraftServer server) {
        List<Item> dyeableItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> item.components().has(DataComponents.DYE))
                .toList();
        Item item = dyeableItems.get(random.nextInt(dyeableItems.size()));
        return item.getDefaultInstance();
    }

    private ItemStack getMapCloningResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.MAP.getDefaultInstance();
    }

    private ItemStack getDecoratedPotResult(RecipeHolder<?> recipe, MinecraftServer server) {
        return Items.DECORATED_POT.getDefaultInstance();
    }

    private ItemStack getRepairItemResult(RecipeHolder<?> recipe, MinecraftServer server) {
        List<Item> damageables = BuiltInRegistries.ITEM.stream()
                .filter(item -> item.components().has(DataComponents.MAX_DAMAGE))
                .toList();
        Item item = damageables.get(random.nextInt(damageables.size()));
        return item.getDefaultInstance();
    }

    private ItemStack getTransmuteResult(RecipeHolder<?> recipe, MinecraftServer server) {
        List<Item> transmutableItems = BuiltInRegistries.ITEM.listElements()
                .filter(ref -> ref.is(ItemTags.SHULKER_BOXES) || ref.is(ItemTags.BUNDLES))
                .map(Holder.Reference::value).toList();
        Item item = transmutableItems.get(random.nextInt(transmutableItems.size()));
        return item.getDefaultInstance();
    }

    private class SmithingTrimResultGetter implements ResultGetter {
        private List<ItemStack> bases;
        private List<ItemStack> additions;

        public ItemStack getResult(RecipeHolder<?> recipe, MinecraftServer server) {
            ItemStack defaultTrimmed = Items.IRON_CHESTPLATE.getDefaultInstance();

            Optional<Holder.Reference<TrimMaterial>> defaultMat = server.registries()
                    .getLayer(RegistryLayer.RELOADABLE)
                    .getOrThrow(Registries.TRIM_MATERIAL)
                    .value()
                    .get(TrimMaterials.REDSTONE);
            Optional<Holder.Reference<TrimPattern>> defaultPat = server.registries()
                    .getLayer(RegistryLayer.RELOADABLE)
                    .getOrThrow(Registries.TRIM_PATTERN)
                    .value()
                    .get(TrimPatterns.COAST);

            defaultTrimmed.set(DataComponents.TRIM, new ArmorTrim(defaultMat.orElseThrow(), defaultPat.orElseThrow()));

            SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) recipe.value();
            ContextMap context = new ContextMap.Builder().create(LootContextParamSets.EMPTY);
            ItemStack template = accessor.trulyrandom$template().display().resolveForFirstStack(context);
            if (bases == null || bases.isEmpty()) {
                Ingredient base = accessor.trulyrandom$base();
                bases = new ArrayList<>(base.items().map(ItemStack::new).toList());
            }
            if (additions == null || additions.isEmpty()) {
                Ingredient addition = accessor.trulyrandom$addition();
                additions = new ArrayList<>(addition.items().map(ItemStack::new).toList());
            }
            ItemStack base = bases.remove(random.nextInt(bases.size()));
            ItemStack addition = additions.remove(random.nextInt(additions.size()));
            SmithingRecipeInput input = new SmithingRecipeInput(template, base, addition);
            return ((SmithingTrimRecipe) recipe.value()).assemble(input);
        }
    }

    @FunctionalInterface
    private interface ResultGetter {
        ItemStack getResult(RecipeHolder<?> recipe, MinecraftServer server);
    }
}
