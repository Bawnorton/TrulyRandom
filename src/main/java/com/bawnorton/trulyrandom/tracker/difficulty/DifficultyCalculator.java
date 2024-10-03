package com.bawnorton.trulyrandom.tracker.difficulty;

import com.bawnorton.trulyrandom.random.loot.LootRandomiser;
import com.bawnorton.trulyrandom.random.recipe.RecipeRandomiser;
import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import com.bawnorton.trulyrandom.tracker.loot.LootTableReader;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropType;
import com.bawnorton.trulyrandom.tracker.loot.drop.DropTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DifficultyCalculator {
    public DifficultyRating calculateDifficulty(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser) {
        return calculateDifficultyForItem(lootRandomiser, recipeRandomiser, Items.ENDER_EYE);
    }

    public DifficultyRating calculateDifficultyForItem(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser, Item item) {
        return calculateDifficultyForItem(lootRandomiser, recipeRandomiser, item, new HashMap<>());
    }

    private DifficultyRating calculateDifficultyForItem(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser, Item item, Map<Item, DifficultyRating> computed) {
        if (computed.containsKey(item)) {
            return computed.get(item);
        }
        List<RecipeEntry<?>> recipeSources = recipeRandomiser.getSources(item.getDefaultStack());
        DifficultyRating lootTableRating = calculateDifficultyFromLootTables(lootRandomiser, recipeRandomiser, item, computed);
        DifficultyRating recipeRating = calculateDifficultyRatingFromRecipes(lootRandomiser, recipeRandomiser, recipeSources, computed);
        return DifficultyRating.eaiser(recipeRating, lootTableRating);
    }

    private DifficultyRating calculateDifficultyForItemSet(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser, Set<Item> itemSet, Map<Item, DifficultyRating> computed) {
        DifficultyRating difficultyRating = DifficultyRating.IMPOSSIBLE;
        for (Item item : itemSet) {
            DifficultyRating newRating = calculateDifficultyForItem(lootRandomiser, recipeRandomiser, item, computed);
            if (newRating.harderThan(difficultyRating)) {
                difficultyRating = newRating;
            }
        }
        return difficultyRating;
    }

    private @NotNull DifficultyRating calculateDifficultyRatingFromRecipes(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser, List<RecipeEntry<?>> recipeSources, Map<Item, DifficultyRating> computed) {
        Set<Set<Item>> itemSets = recipeSources.stream()
                .map(RecipeEntry::value)
                .map(recipe -> recipe.getIngredients()
                        .stream()
                        .flatMap(ingredient -> Arrays.stream(ingredient.getMatchingStacks()))
                        .map(ItemStack::getItem)
                        .collect(Collectors.toSet()))
                .collect(Collectors.toSet());
        DifficultyRating difficultyRating = DifficultyRating.IMPOSSIBLE;
        for (Set<Item> itemSet : itemSets) {
            DifficultyRating newRating = calculateDifficultyForItemSet(lootRandomiser, recipeRandomiser, itemSet, computed);
            if (newRating.easierThan(difficultyRating)) {
                difficultyRating = newRating;
            }
        }
        return difficultyRating;
    }

    private DifficultyRating calculateDifficultyFromLootTables(LootRandomiser lootRandomiser, RecipeRandomiser recipeRandomiser, Item item, Map<Item, DifficultyRating> computed) {
        List<RegistryKey<LootTable>> sources = lootRandomiser.getSourcesOfItem(item);
        DifficultyRating difficultyRating = DifficultyRating.IMPOSSIBLE;
        for(RegistryKey<LootTable> source : sources) {
            DropType dropType = DropTypes.getDropType(LootTableIdentifier.from(source.getValue()));
            DifficultyRating newRating = dropType.baseRating();
            computed.put(item, newRating);
            Registry<LootTable> registry = lootRandomiser.getLootTableRegistry();
            List<Item> items = LootTableReader.read(registry, registry.get(source));
            for(Item i : items) {
                DifficultyRating rating = calculateDifficultyForItem(lootRandomiser, recipeRandomiser, i, computed);
                if(rating.easierThan(newRating)) {
                    difficultyRating = newRating;
                }
            }
        }
        return difficultyRating;
    }
}
