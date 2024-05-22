package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.mixin.accessor.RecipeManagerAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.RecipeTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeRandomiser extends ServerRandomiserModule {
    private final Map<UUID, RecipeTracker> trackers = new HashMap<>();
    private final ResultManager resultManager = new ResultManager();
    private final Map<Identifier, ItemStack> originalOutputs = new HashMap<>();
    private Map<ServerPlayerEntity, Collection<RecipeEntry<?>>> playerKnownRecipes;

    public RecipeRandomiser(MinecraftServer server) {
        this.playerKnownRecipes = getPlayerKnownRecipes(server);
        long seed = TrulyRandom.getRandomiser(server).getModules().getSeed(Module.RECIPES);
        ((RecipeManagerAccessor) server.getRecipeManager()).getRecipesById().forEach((id, recipe) -> {
            ItemStack result = resultManager.getResult(recipe, server, seed);
            originalOutputs.put(id, result);
        });
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        playerKnownRecipes = getPlayerKnownRecipes(server);
        resetRecipeManager(server);
        List<Map.Entry<Identifier, RecipeEntry<?>>> recipeEntries = new ArrayList<>(((RecipeManagerAccessor) server.getRecipeManager()).getRecipesById().entrySet());
        List<RecipeEntry<?>> newRecipeEntries = new ArrayList<>();
        Map<Identifier, RecipeEntry<?>> recipes = new HashMap<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (Map.Entry<Identifier, RecipeEntry<?>> recipeEntry : recipeEntries) {
            Identifier id = recipeEntry.getKey();
            RecipeEntry<?> recipe = recipeEntry.getValue();
            ItemStack result = resultManager.getResult(recipe, server, seed);
            recipes.put(id, recipe);
            outputs.add(result);
        }
        Collections.shuffle(outputs, new Random(seed));
        for (int i = 0; i < outputs.size(); i++) {
            ItemStack output = outputs.get(i);
            Identifier id = recipeEntries.get(i).getKey();
            RecipeEntry<?> recipe = recipes.get(id);
            RecipeEntry<?> newRecipe = resultManager.setResult(recipe, output);
            newRecipeEntries.add(newRecipe);
        }

        server.getRecipeManager().setRecipes(newRecipeEntries);
        resyncPlayerRecipes(server);
    }

    @Override
    public void reset(MinecraftServer server) {
        playerKnownRecipes = getPlayerKnownRecipes(server);
        resetRecipeManager(server);
        resyncPlayerRecipes(server);
    }

    private void resetRecipeManager(MinecraftServer server) {
        List<Map.Entry<Identifier, RecipeEntry<?>>> recipeEntries = new ArrayList<>(((RecipeManagerAccessor) server.getRecipeManager()).getRecipesById().entrySet());
        List<RecipeEntry<?>> newRecipeEntries = new ArrayList<>();
        for (Map.Entry<Identifier, RecipeEntry<?>> recipeEntry : recipeEntries) {
            RecipeEntry<?> recipe = recipeEntry.getValue();
            ItemStack result = originalOutputs.get(recipeEntry.getKey());
            RecipeEntry<?> newRecipe = resultManager.clearOrSetResult(recipe, result);
            newRecipeEntries.add(newRecipe);
        }
        server.getRecipeManager().setRecipes(newRecipeEntries);
    }

    @Override
    public RecipeTracker getTracker(PlayerEntity player) {
        return trackers.get(player.getUuid());
    }

    @Override
    public List<RecipeTracker> getTrackers() {
        return new ArrayList<>(trackers.values());
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
        for (ServerPlayerEntity player : players) {
            Collection<RecipeEntry<?>> knownRecipes = recipes.stream()
                    .filter(player.getRecipeBook()::contains)
                    .collect(Collectors.toList());
            playerKnownRecipes.put(player, knownRecipes);
        }
        return playerKnownRecipes;
    }

    @Override
    public Module getModule() {
        return Module.RECIPES;
    }
}
