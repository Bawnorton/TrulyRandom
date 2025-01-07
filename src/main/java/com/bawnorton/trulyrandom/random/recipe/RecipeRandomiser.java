package com.bawnorton.trulyrandom.random.recipe;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.mixin.accessor.PreparedRecipesAccessor;
import com.bawnorton.trulyrandom.mixin.accessor.ServerRecipeManagerAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.RecipeModuleState;
import com.bawnorton.trulyrandom.random.module.ServerRandomiserModule;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.DataResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipePropertySet;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public class RecipeRandomiser extends ServerRandomiserModule {
    private final Map<Team, RecipeTracker> trackers = new HashMap<>();
    private final ResultManager resultManager = new ResultManager();
    private final Map<RegistryKey<Recipe<?>>, ItemStack> originalOutputs = new HashMap<>();
    private final Function<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipeRegistry;
    private final Map<Item, List<RegistryKey<Recipe<?>>>> outputToRecipes = new HashMap<>();

    public RecipeRandomiser(MinecraftServer server, long seed) {
        resultManager.setRandom(seed);
        getRecipes(server).forEach((key, recipe) -> {
            ItemStack result = resultManager.getResult(recipe, server);
            originalOutputs.put(key, result);
        });
        recipeRegistry = key -> getRecipes(server).get(key);
    }

    public void trackRecipeOutput(Team team, RegistryKey<Recipe<?>> recipe, ItemStack result) {
        trackers.computeIfAbsent(team, k -> {
            RecipeTracker tracker = new RecipeTracker();
            tracker.setRecipeRegistry(recipeRegistry);
            tracker.setTeam(k);
            return tracker;
        }).track(recipe, result);
    }

    private Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> getRecipes(MinecraftServer server) {
        return ((PreparedRecipesAccessor) ((ServerRecipeManagerAccessor) server.getRecipeManager()).getPreparedRecipes()).getByKey();
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList trackerNbt = new NbtList();
        trackers.forEach((team, tracker) -> {
            DataResult<NbtElement> result = RecipeTracker.CODEC.encodeStart(NbtOps.INSTANCE, tracker);
            result.result().ifPresent(trackerNbt::add);
            result.error().ifPresent(e -> TrulyRandom.LOGGER.error(e.message()));
        });
        nbt.put("trackers", trackerNbt);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        NbtList trackerNbt = nbt.getList("trackers", NbtElement.COMPOUND_TYPE);
        for (NbtElement element : trackerNbt) {
            DataResult<RecipeTracker> result = RecipeTracker.CODEC.parse(NbtOps.INSTANCE, element);
            result.result().ifPresent(tracker -> {
                trackers.put(tracker.getTeam(), tracker);
                tracker.setRecipeRegistry(recipeRegistry);
            });
            result.error().ifPresent(e -> TrulyRandom.LOGGER.error(e.message()));
        }
    }

    @Override
    public void randomise(MinecraftServer server, long seed) {
        randomiseRecipeManager(server, seed);
        resyncPlayerRecipes(server);
    }

    @Override
    public void reset(MinecraftServer server) {
        resetRecipeManager(server);
        resyncPlayerRecipes(server);
    }

    private void randomiseRecipeManager(MinecraftServer server, long seed) {
        resultManager.setRandom(seed);
        resetRecipeManager(server);

        List<Map.Entry<RegistryKey<Recipe<?>>, RecipeEntry<?>>> recipeEntries = new ArrayList<>(getRecipes(server).entrySet());
        RecipeModuleState moduleState = TrulyRandom.getRandomiser(server).getModules().getState(Module.RECIPES, RecipeModuleState.class);
        List<RecipeMetadata> newRecipes = new ArrayList<>();
        recipeEntries = recipeEntries.stream()
                .filter(entry -> {
                    if(!moduleState.isRecipeTypeEnabled(entry.getValue().value().getType())) {
                        newRecipes.add(new RecipeMetadata(entry.getValue()));
                        return false;
                    }
                    return true;
                })
                .toList();
        Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipes = new HashMap<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (Map.Entry<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipeEntry : recipeEntries) {
            RegistryKey<Recipe<?>> key = recipeEntry.getKey();
            RecipeEntry<?> recipe = recipeEntry.getValue();
            ItemStack result = resultManager.getResult(recipe, server);
            recipes.put(key, recipe);
            outputs.add(result);
        }
        Collections.shuffle(outputs, new Random(seed));
        outputToRecipes.clear();
        for (int i = 0; i < outputs.size(); i++) {
            ItemStack output = outputs.get(i);
            RegistryKey<Recipe<?>> key = recipeEntries.get(i).getKey();
            outputToRecipes.computeIfAbsent(output.getItem(), k -> new ArrayList<>()).add(key);
            RecipeEntry<?> recipe = recipes.get(key);
            RecipeEntry<?> newRecipe = resultManager.setResult(recipe, output);
            newRecipes.add(new RecipeMetadata(newRecipe));
        }

        updateRecipes(server, newRecipes);
    }

    private void resetRecipeManager(MinecraftServer server) {
        List<Map.Entry<RegistryKey<Recipe<?>>, RecipeEntry<?>>> recipeEntries = new ArrayList<>(getRecipes(server).entrySet());
        List<RecipeMetadata> newRecipes = new ArrayList<>();
        outputToRecipes.clear();
        for (Map.Entry<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipeEntry : recipeEntries) {
            RecipeEntry<?> recipe = recipeEntry.getValue();
            ItemStack result = originalOutputs.get(recipeEntry.getKey());
            RecipeEntry<?> newRecipe = resultManager.clearOrSetResult(recipe, result);
            outputToRecipes.computeIfAbsent(result.getItem(), k -> new ArrayList<>()).add(recipeEntry.getKey());
            newRecipes.add(new RecipeMetadata(newRecipe));
        }
        updateRecipes(server, newRecipes);
    }

    private void updateRecipes(MinecraftServer server, List<RecipeMetadata> recipes) {
        ServerRecipeManager manager = server.getRecipeManager();
        PreparedRecipes preparedRecipes = ((ServerRecipeManagerAccessor) manager).getPreparedRecipes();
        PreparedRecipesAccessor accessor = (PreparedRecipesAccessor) preparedRecipes;
        Map<RegistryKey<Recipe<?>>, RecipeEntry<?>> newByKey = new HashMap<>();
        Multimap<RecipeType<?>, RecipeEntry<?>> newByType = HashMultimap.create();
        for(RecipeMetadata metadata : recipes) {
            RecipeEntry<?> entry = metadata.entry();
            newByKey.put(metadata.key(), entry);
            newByType.put(metadata.type(), entry);
        }
        accessor.setByKey(newByKey);
        accessor.setByType(newByType);
        manager.initialize(server.getSaveProperties().getEnabledFeatures());
    }

    @Override
    public RecipeTracker getTracker(TeamMember teamMember) {
        return trackers.get(teamMember.trulyrandom$getTeam());
    }

    @Override
    public List<RecipeTracker> getTrackers() {
        return new ArrayList<>(trackers.values());
    }

    public List<RegistryKey<Recipe<?>>> getRecipesForOutput(Item item) {
        return outputToRecipes.getOrDefault(item, List.of());
    }

    private void resyncPlayerRecipes(MinecraftServer server) {
        ServerRecipeManager manager = server.getRecipeManager();
        Map<RegistryKey<RecipePropertySet>, RecipePropertySet> propertySets = manager.getPropertySets();
        CuttingRecipeDisplay.Grouping<StonecuttingRecipe> stonecutterRecipes = manager.getStonecutterRecipeForSync();
        server.getPlayerManager().getPlayerList().forEach(player -> {
            player.networkHandler.sendPacket(new SynchronizeRecipesS2CPacket(propertySets, stonecutterRecipes));
            // refreshes the client recipe book
            player.getRecipeBook().sendInitRecipesPacket(player);
        });
    }

    @Override
    public Module getModule() {
        return Module.RECIPES;
    }
}
