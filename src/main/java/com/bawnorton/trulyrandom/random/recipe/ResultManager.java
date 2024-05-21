package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.bawnorton.trulyrandom.extend.ResultSetter;
import com.bawnorton.trulyrandom.mixin.accessor.SmithingTrimRecipeAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResultManager {
    private final Map<Class<? extends Recipe<?>>, ResultGetter> getters = new HashMap<>();
    private Random random;

    public ResultManager() {
        getters.put(SmithingTrimRecipe.class, new SmithingTrimResultGetter());
        getters.put(SuspiciousStewRecipe.class, this::getSuspiciousStewResult);
        getters.put(BookCloningRecipe.class, this::getBookCloningResult);
        getters.put(BannerDuplicateRecipe.class, this::getBannerDuplicateResult);
        getters.put(FireworkStarFadeRecipe.class, this::getFireworkStarFadeResult);
        getters.put(FireworkStarRecipe.class, this::getFireworkStarResult);
        getters.put(ShieldDecorationRecipe.class, this::getShieldDecorationResult);
        getters.put(TippedArrowRecipe.class, this::getTippedArrowResult);
        getters.put(ArmorDyeRecipe.class, this::getArmorDyeResult);
        getters.put(MapCloningRecipe.class, this::getMapCloningResult);
        getters.put(CraftingDecoratedPotRecipe.class, this::getDecoratedPotResult);
        getters.put(RepairItemRecipe.class, this::getRepairItemResult);
        getters.put(ShulkerBoxColoringRecipe.class, this::getShulkerBoxColoringResult);
    }

    public ItemStack getResult(RecipeEntry<?> recipe, MinecraftServer server) {
        random = new Random(TrulyRandom.getRandomiser(server).getModules().getSeed(Module.RECIPES));
        //noinspection SuspiciousMethodCalls
        return getters.getOrDefault(recipe.value().getClass(), this::getNormalResult).getResult(recipe, server);
    }

    public RecipeEntry<?> setResult(RecipeEntry<?> recipe, ItemStack newResult) {
        if (recipe.value() instanceof ResultSetter resultSetter) {
            resultSetter.trulyrandom$setResult(newResult);
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
        return recipe.value().getResult(server.getRegistryManager());
    }

    private ItemStack getSuspiciousStewResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<SuspiciousStewIngredient> ingredients = SuspiciousStewIngredient.getAll();
        SuspiciousStewIngredient ingredient = ingredients.get(random.nextInt(ingredients.size()));
        ItemStack suspiciousStew = Items.SUSPICIOUS_STEW.getDefaultStack();
        suspiciousStew.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, ingredient.getStewEffects());
        return suspiciousStew;
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

    private ItemStack getShulkerBoxColoringResult(RecipeEntry<?> recipe, MinecraftServer server) {
        List<Item> shulkerBoxes = Registries.BLOCK.stream()
                .filter(ShulkerBoxBlock.class::isInstance)
                .map(Block::asItem)
                .toList();
        Item item = shulkerBoxes.get(random.nextInt(shulkerBoxes.size()));
        return item.getDefaultStack();
    }


    private class SmithingTrimResultGetter implements ResultGetter {
        private List<ItemStack> bases;
        private List<ItemStack> additions;

        public ItemStack getResult(RecipeEntry<?> recipe, MinecraftServer server) {
            SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) recipe.value();
            ItemStack template = accessor.getTemplate().getMatchingStacks()[0];
            if (bases == null || bases.isEmpty()) {
                bases = Stream.of(accessor.getBase().getMatchingStacks())
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            }
            if (additions == null || additions.isEmpty()) {
                additions = Stream.of(accessor.getAddition().getMatchingStacks())
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
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
