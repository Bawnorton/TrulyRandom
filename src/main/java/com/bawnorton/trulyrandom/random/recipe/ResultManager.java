package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ResultClearer;
import com.bawnorton.trulyrandom.extend.ResultSetter;
import com.bawnorton.trulyrandom.mixin.accessor.SmithingTrimRecipeAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SuspiciousStewIngredient;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;

import java.util.*;
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

    public ItemStack getResult(Recipe<?> recipe, MinecraftServer server) {
        random = new Random(TrulyRandom.getRandomiser(server).getModules().getSeed(Module.RECIPES));
        return getters.getOrDefault(recipe.getClass(), this::getNormalResult).getResult(recipe, server);
    }

    public Recipe<?> setResult(Recipe<?> recipe, ItemStack newResult) {
        if (recipe instanceof ResultSetter resultSetter) {
            resultSetter.trulyrandom$setResult(newResult);
            return (Recipe<?>) resultSetter;
        }
        return recipe;
    }

    public Recipe<?> clearOrSetResult(Recipe<?> recipe, ItemStack result) {
        if (recipe instanceof ResultClearer resultClearer) {
            resultClearer.trulyrandom$clearResult();
            return (Recipe<?>) resultClearer;
        }
        return setResult(recipe, result);
    }

    private ItemStack getNormalResult(Recipe<?> recipe, MinecraftServer server) {
        return recipe.getOutput(server.getRegistryManager());
    }

    private ItemStack getSuspiciousStewResult(Recipe<?> recipe, MinecraftServer server) {
        List<SuspiciousStewIngredient> ingredients = SuspiciousStewIngredient.getAll();
        SuspiciousStewIngredient ingredient = ingredients.get(new Random().nextInt(ingredients.size()));
        ItemStack suspiciousStew = Items.SUSPICIOUS_STEW.getDefaultStack();
        SuspiciousStewItem.addEffectToStew(suspiciousStew, ingredient.getEffectInStew(), ingredient.getEffectInStewDuration());
        return suspiciousStew;
    }

    private ItemStack getBookCloningResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.WRITABLE_BOOK.getDefaultStack();
    }

    private ItemStack getBannerDuplicateResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.WHITE_BANNER.getDefaultStack();
    }

    private ItemStack getFireworkStarFadeResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultStack();
    }

    private ItemStack getFireworkStarResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.FIREWORK_STAR.getDefaultStack();
    }

    private ItemStack getShieldDecorationResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.SHIELD.getDefaultStack();
    }

    private ItemStack getTippedArrowResult(Recipe<?> recipe, MinecraftServer server) {
        Potion potion = Registries.POTION.get(new Random().nextInt(Registries.POTION.getIds().size()));
        ItemStack arrow = Items.TIPPED_ARROW.getDefaultStack();
        arrow.setCount(8);
        PotionUtil.setPotion(arrow, potion);
        PotionUtil.setCustomPotionEffects(arrow, potion.getEffects());
        return arrow;
    }

    private ItemStack getArmorDyeResult(Recipe<?> recipe, MinecraftServer server) {
        List<Item> dyeableItems = Registries.ITEM.stream().filter(DyeableItem.class::isInstance).toList();
        Item item = dyeableItems.get(new Random().nextInt(dyeableItems.size()));
        return item.getDefaultStack();
    }

    private ItemStack getMapCloningResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.MAP.getDefaultStack();
    }

    private ItemStack getDecoratedPotResult(Recipe<?> recipe, MinecraftServer server) {
        return Items.DECORATED_POT.getDefaultStack();
    }

    private ItemStack getRepairItemResult(Recipe<?> recipe, MinecraftServer server) {
        List<Item> damageables = Registries.ITEM.stream().filter(Item::isDamageable).toList();
        Item item = damageables.get(new Random().nextInt(damageables.size()));
        return item.getDefaultStack();
    }

    private ItemStack getShulkerBoxColoringResult(Recipe<?> recipe, MinecraftServer server) {
        List<Item> shulkerBoxes = Registries.BLOCK.stream()
                .filter(ShulkerBoxBlock.class::isInstance)
                .map(Block::asItem)
                .toList();
        Item item = shulkerBoxes.get(random.nextInt(shulkerBoxes.size()));
        return item.getDefaultStack();
    }

    @FunctionalInterface
    private interface ResultGetter {
        ItemStack getResult(Recipe<?> recipe, MinecraftServer server);
    }

    private class SmithingTrimResultGetter implements ResultGetter {
        private List<ItemStack> bases;
        private List<ItemStack> additions;

        public ItemStack getResult(Recipe<?> recipe, MinecraftServer server) {
            SmithingTrimRecipeAccessor accessor = (SmithingTrimRecipeAccessor) recipe;
            ItemStack template = accessor.getTemplate().getMatchingStacks()[0];
            if (bases == null || bases.isEmpty()) bases = Stream.of(accessor.getBase().getMatchingStacks())
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            if (additions == null || additions.isEmpty())
                additions = Stream.of(accessor.getAddition().getMatchingStacks())
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            ItemStack base = bases.remove(random.nextInt(bases.size()));
            ItemStack addition = additions.remove(random.nextInt(additions.size()));
            Inventory inventory = new SimpleInventory(template, base, addition);
            return ((SmithingTrimRecipe) recipe).craft(inventory, server.getRegistryManager());
        }
    }
}
