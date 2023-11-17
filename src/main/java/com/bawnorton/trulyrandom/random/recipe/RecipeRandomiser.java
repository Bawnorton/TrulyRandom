package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.random.Module;
import com.bawnorton.trulyrandom.random.ServerRandomiserModule;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeRandomiser extends ServerRandomiserModule {
    private final ResultManager resultManager;
    private final Map<Identifier, ItemStack> originalOutputs;
    private Map<ServerPlayerEntity, Collection<RecipeEntry<?>>> playerKnownRecipes;

    public RecipeRandomiser(MinecraftServer server) {
        this.playerKnownRecipes = getPlayerKnownRecipes(server);
        this.originalOutputs = new HashMap<>();
        this.resultManager = new ResultManager();
        server.getRecipeManager().values().forEach(recipeEntry -> {
            Identifier id = recipeEntry.id();
            Recipe<?> recipe = recipeEntry.value();
            ItemStack result = resultManager.getResult(recipe, server);
            originalOutputs.put(id, result);
        });

    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        playerKnownRecipes = getPlayerKnownRecipes(server);
        resetRecipeManager(server);
        List<RecipeEntry<?>> recipeEntries = new ArrayList<>(server.getRecipeManager().values());
        List<RecipeEntry<?>> newRecipeEntries = new ArrayList<>();
        Map<Identifier, Recipe<?>> recipes = new HashMap<>();
        List<ItemStack> outputs = new ArrayList<>();
        for(RecipeEntry<?> recipeEntry: recipeEntries) {
            Identifier id = recipeEntry.id();
            Recipe<?> recipe = recipeEntry.value();
            ItemStack result = resultManager.getResult(recipe, server);
            recipes.put(id, recipe);
            outputs.add(result);
        }
        Collections.shuffle(outputs, new Random(seed));
        for(int i = 0; i < outputs.size(); i++) {
            ItemStack output = outputs.get(i);
            Identifier id = recipeEntries.get(i).id();
            Recipe<?> recipe = recipes.get(id);
            Recipe<?> newRecipe = resultManager.setResult(recipe, output);
            newRecipeEntries.add(new RecipeEntry<>(id, newRecipe));
        }

        server.getRecipeManager().setRecipes(newRecipeEntries);
        resyncPlayerRecipes(server);
        setRandomised(true);
    }

    @Override
    public void reset(MinecraftServer server) {
        playerKnownRecipes = getPlayerKnownRecipes(server);
        resetRecipeManager(server);
        resyncPlayerRecipes(server);
        setRandomised(false);
    }

    private void resetRecipeManager(MinecraftServer server) {
        Collection<RecipeEntry<?>> recipeEntries = server.getRecipeManager().values();
        List<RecipeEntry<?>> newRecipeEntries = new ArrayList<>();
        for(RecipeEntry<?> recipeEntry: recipeEntries) {
            Recipe<?> recipe = recipeEntry.value();
            ItemStack result = originalOutputs.get(recipeEntry.id());
            newRecipeEntries.add(new RecipeEntry<>(recipeEntry.id(), resultManager.clearOrSetResult(recipe, result)));
        }
        server.getRecipeManager().setRecipes(newRecipeEntries);
    }

    private void resyncPlayerRecipes(MinecraftServer server) {
        Collection<RecipeEntry<?>> recipes = server.getRecipeManager().values();
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.networkHandler.sendPacket(new SynchronizeRecipesS2CPacket(recipes));
            // refreshes the recipe book
            player.getRecipeBook().lockRecipes(playerKnownRecipes.get(player), player);
            player.getRecipeBook().unlockRecipes(playerKnownRecipes.get(player), player);
        });
    }

    private Map<ServerPlayerEntity, Collection<RecipeEntry<?>>> getPlayerKnownRecipes(MinecraftServer server) {
        RecipeManager recipeManager = server.getRecipeManager();
        Collection<RecipeEntry<?>> recipes = recipeManager.values();
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        Map<ServerPlayerEntity, Collection<RecipeEntry<?>>> playerKnownRecipes = new HashMap<>();
        for(ServerPlayerEntity player: players) {
            Collection<RecipeEntry<?>> knownRecipes = recipes.stream().filter(player.getRecipeBook()::contains).collect(Collectors.toList());
            playerKnownRecipes.put(player, knownRecipes);
        }
        return playerKnownRecipes;
    }

    @Override
    public Module getModule() {
        return Module.RECIPES;
    }
}
