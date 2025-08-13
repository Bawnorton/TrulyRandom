package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.bawnorton.trulyrandom.extend.ResultHolder;
import com.bawnorton.trulyrandom.mixin.accessor.SmithingTrimRecipeAccessor;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.item.equipment.trim.ArmorTrimPatterns;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.context.ContextParameterMap;
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
        getters.put(TippedArrowRecipe.class, this::getTippedArrowResult);
        getters.put(ArmorDyeRecipe.class, this::getArmorDyeResult);
        getters.put(MapCloningRecipe.class, this::getMapCloningResult);
        getters.put(MapExtendingRecipe.class, this::getMapCloningResult);
        getters.put(CraftingDecoratedPotRecipe.class, this::getDecoratedPotResult);
        getters.put(RepairItemRecipe.class, this::getRepairItemResult);
        getters.put(TransmuteRecipe.class, this::getTransmuteResult);
    }

    public void setRandom(long seed) {
        random = new Random(seed);
    }

    public ItemStack getResult(RecipeEntry<?> recipe, MinecraftServer server) {
        ItemStack result = getters.getOrDefault(recipe.value().getClass(), this::getNormalResult).getResult(recipe, server);
        if(result.isEmpty()) {
            result = Items.PAPER.getDefaultStack();
            result.set(DataComponentTypes.CUSTOM_NAME, Text.literal("This is here so the game doesn't crash"));
            result.set(DataComponentTypes.LORE, new LoreComponent(List.of(Text.literal("Unknown recipe type: " + recipe.id().toString()))));
            TrulyRandom.LOGGER.warn("Unknown recipe type: {}", recipe.value().getClass().getSimpleName());
        }
        return result;
    }

    public RecipeEntry<?> setResult(RecipeEntry<?> recipe, ItemStack newResult) {
        if (recipe.value() instanceof ResultHolder resultHolder) {
            resultHolder.trulyrandom$setResult(newResult);
            return recipe;
        }
        return recipe;
    }

    public RecipeEntry<?> clearOrSetResult(RecipeEntry<?> recipe, ItemStack result) {
        if (recipe.value() instanceof ResultClearer resultClearer) {
            resultClearer.trulyrandom$clearResult();
            return recipe;
        }
        return setResult(recipe, result);
    }

    private ItemStack getNormalResult(RecipeEntry<?> recipe, MinecraftServer server) {
        if(recipe.value() instanceof ResultHolder resultHolder) {
            return resultHolder.trulyrandom$getResult();
        }
        throw new UnsupportedOperationException("Recipe type \"" + recipe.value().getClass().getSimpleName() + "\" is not supported.");
    }

    private ItemStack getBookCloningResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.WRITABLE_BOOK.getDefaultStack();
    }

    private ItemStack getBannerDuplicateResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.WHITE_BANNER.getDefaultStack();
    }

    private ItemStack getFireworkStarFadeResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultStack();
    }

    private ItemStack getFireworkStarResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultStack();
    }

    private ItemStack getFireworkRocketResult(RecipeEntry<?> recipeEntry, MinecraftServer server) {
        return Items.FIREWORK_ROCKET.getDefaultStack();
    }

    private ItemStack getShieldDecorationResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.SHIELD.getDefaultStack();
    }

    private ItemStack getTippedArrowResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<RegistryEntry<Potion>> list = Registries.POTION
                .streamEntries()
                .filter(entry -> !entry.value().getEffects().isEmpty())
                .collect(Collectors.toList());
        RegistryEntry<Potion> registryEntry = list.get(random.nextInt(list.size()));
        ItemStack arrow = Items.TIPPED_ARROW.getDefaultStack();
        arrow.setCount(8);
        arrow.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(registryEntry));
        return arrow;
    }

    private ItemStack getArmorDyeResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<Item> dyeableItems = Registries.ITEM.streamEntries().filter(ref -> ref.isIn(ItemTags.DYEABLE)).map(RegistryEntry.Reference::value).toList();
        Item item = dyeableItems.get(random.nextInt(dyeableItems.size()));
        return item.getDefaultStack();
    }

    private ItemStack getMapCloningResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.MAP.getDefaultStack();
    }

    private ItemStack getDecoratedPotResult(RecipeEntry<?> recipe, MinecraftServer server) {
        return Items.DECORATED_POT.getDefaultStack();
    }

    private ItemStack getRepairItemResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<Item> damageables = Registries.ITEM.stream().filter(item -> item.getComponents().contains(DataComponentTypes.MAX_DAMAGE)).toList();
        Item item = damageables.get(random.nextInt(damageables.size()));
        return item.getDefaultStack();
    }

    private ItemStack getTransmuteResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<Item> transmutableItems = Registries.ITEM.streamEntries().filter(ref -> ref.isIn(ItemTags.SHULKER_BOXES) || ref.isIn(ItemTags.BUNDLES)).map(RegistryEntry.Reference::value).toList();
        Item item = transmutableItems.get(random.nextInt(transmutableItems.size()));
        return item.getDefaultStack();
    }

    private class SmithingTrimResultGetter implements ResultGetter {
        private List<ItemStack> bases;
        private List<ItemStack> additions;

        public ItemStack getResult(RecipeEntry<?> recipe, MinecraftServer server) {
            ItemStack defaultTrimmed = Items.IRON_CHESTPLATE.getDefaultStack();
            Optional<RegistryEntry<ArmorTrimMaterial>> defaultMat = ArmorTrimMaterials.get(server.getRegistryManager(), Items.REDSTONE.getDefaultStack());
            Optional<RegistryEntry.Reference<ArmorTrimPattern>> defaultPat = server.getRegistryManager()
                    .getOrThrow(RegistryKeys.TRIM_PATTERN)
                    .getEntry(ArmorTrimPatterns.COAST.getValue());
            defaultTrimmed.set(DataComponentTypes.TRIM, new ArmorTrim(defaultMat.orElseThrow(), defaultPat.orElseThrow()));

            SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) recipe.value();
            ContextParameterMap context = new ContextParameterMap.Builder().build(LootContextTypes.EMPTY);
            ItemStack template = accessor.getTemplate().toDisplay().getFirst(context);
            if (bases == null || bases.isEmpty()) {
                Ingredient base = accessor.getBase();
                bases = new ArrayList<>(base.getMatchingItems().map(ItemStack::new).toList());
            }
            if (additions == null || additions.isEmpty()) {
                Ingredient addition = accessor.getAddition();
                additions = new ArrayList<>(addition.getMatchingItems().map(ItemStack::new).toList());
            }
            ItemStack base = bases.remove(random.nextInt(bases.size()));
            ItemStack addition = additions.remove(random.nextInt(additions.size()));
            SmithingRecipeInput input = new SmithingRecipeInput(template, base, addition);
            return ((SmithingTrimRecipe) recipe.value()).craft(input, server.getRegistryManager());
        }
    }

    @FunctionalInterface
    private interface ResultGetter {
        ItemStack getResult(RecipeEntry<?> recipe, MinecraftServer server);
    }
}
