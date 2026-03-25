package com.bawnorton.trulyrandom.tracker.recipe;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RecipeTracker extends Tracker<ResourceKey<Recipe<?>>, ItemStack> {
    public static final ThreadLocal<ItemStack> LAST_RECIPE_OUTPUT = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeTracker> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ResourceKey.streamCodec(Registries.RECIPE),
                    ItemStack.STREAM_CODEC
            ), RecipeTracker::known,
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.registry(Registries.ITEM),
                    ByteBufCodecs.collection(ArrayList::new, RecipeHolder.STREAM_CODEC)
            ), tracker -> tracker.recipesByOutput,
            Team.STREAM_CODEC, Tracker::getTeam,
            RecipeTracker::new
    );

    public static final Codec<RecipeTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    ResourceKey.codec(Registries.RECIPE),
                    ItemStack.CODEC
            ).fieldOf("known_recipes").forGetter(RecipeTracker::known),
            Team.CODEC.fieldOf("team").forGetter(tracker -> tracker.team)
    ).apply(instance, RecipeTracker::new));

    private final Map<ResourceKey<Recipe<?>>, ItemStack> knownRecipes;
    private final Map<Item, List<RecipeHolder<?>>> recipesByOutput;
    private Function<ResourceKey<Recipe<?>>, RecipeHolder<?>> recipeRegistry;

    public RecipeTracker(Map<ResourceKey<Recipe<?>>, ItemStack> knownRecipes, Map<Item, List<RecipeHolder<?>>> recipesByOutput, Team team) {
        super(team);
        this.knownRecipes = new HashMap<>(knownRecipes);
        this.recipesByOutput = new HashMap<>(recipesByOutput);
    }

    public RecipeTracker(Map<ResourceKey<Recipe<?>>, ItemStack> knownRecipes, Team team) {
        this(knownRecipes, new HashMap<>(), team);
    }

    public RecipeTracker() {
        super(null);
        this.knownRecipes = new HashMap<>();
        this.recipesByOutput = new HashMap<>();
    }


    public void setRecipeRegistry(Function<ResourceKey<Recipe<?>>, RecipeHolder<?>> registry) {
        this.recipeRegistry = registry;
        this.recipesByOutput.clear();
        this.knownRecipes.forEach((id, output) -> recipesByOutput.computeIfAbsent(output.getItem(), k -> new ArrayList<>()).add(registry.apply(id)));
    }

    @Override
    public void track(ResourceKey<Recipe<?>> from, ItemStack to) {
        knownRecipes.put(from, to);
        recipesByOutput.computeIfAbsent(to.getItem(), k -> new ArrayList<>()).add(recipeRegistry.apply(from));
        this.markDirty();
    }

    @Override
    public Map<ResourceKey<Recipe<?>>, ItemStack> known() {
        return knownRecipes;
    }

    @Override
    public void reset() {
        knownRecipes.clear();
        recipesByOutput.clear();
        this.markDirty();
    }

    public List<RecipeHolder<?>> getRecipesFor(Item item) {
        return recipesByOutput.getOrDefault(item, List.of());
    }

    public List<Item> getAllOutputs() {
        return new ArrayList<>(recipesByOutput.keySet());
    }

    public boolean knowsRecipesFor(Item item) {
        return recipesByOutput.containsKey(item);
    }
}
