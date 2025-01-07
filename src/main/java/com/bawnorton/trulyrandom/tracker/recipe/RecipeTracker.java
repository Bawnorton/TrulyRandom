package com.bawnorton.trulyrandom.tracker.recipe;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RecipeTracker extends Tracker<RegistryKey<Recipe<?>>, ItemStack> {
    public static final ThreadLocal<ItemStack> LAST_RECIPE_OUTPUT = ThreadLocal.withInitial(() -> ItemStack.EMPTY);
    public static final PacketCodec<RegistryByteBuf, RecipeTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    HashMap::new,
                    RegistryKey.createPacketCodec(RegistryKeys.RECIPE),
                    ItemStack.PACKET_CODEC
            ), RecipeTracker::known,
            PacketCodecs.map(
                    HashMap::new,
                    PacketCodecs.registryValue(RegistryKeys.ITEM),
                    PacketCodecs.collection(ArrayList::new, RecipeEntry.PACKET_CODEC)
            ), tracker -> tracker.recipesByOutput,
            Team.PACKET_CODEC, Tracker::getTeam,
            RecipeTracker::new
    );

    public static final Codec<RecipeTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(
                    RegistryKey.createCodec(RegistryKeys.RECIPE),
                    ItemStack.CODEC
            ).fieldOf("known_recipes").forGetter(RecipeTracker::known),
            Team.CODEC.fieldOf("team").forGetter(tracker -> tracker.team)
    ).apply(instance, RecipeTracker::new));

    private final Map<RegistryKey<Recipe<?>>, ItemStack> knownRecipes;
    private final Map<Item, List<RecipeEntry<?>>> recipesByOutput;
    private Function<RegistryKey<Recipe<?>>, RecipeEntry<?>> recipeRegistry;

    public RecipeTracker(Map<RegistryKey<Recipe<?>>, ItemStack> knownRecipes, Map<Item, List<RecipeEntry<?>>> recipesByOutput, Team team) {
        super(team);
        this.knownRecipes = new HashMap<>(knownRecipes);
        this.recipesByOutput = new HashMap<>(recipesByOutput);
    }

    public RecipeTracker(Map<RegistryKey<Recipe<?>>, ItemStack> knownRecipes, Team team) {
        this(knownRecipes, new HashMap<>(), team);
    }

    public RecipeTracker() {
        super(null);
        this.knownRecipes = new HashMap<>();
        this.recipesByOutput = new HashMap<>();
    }


    public void setRecipeRegistry(Function<RegistryKey<Recipe<?>>, RecipeEntry<?>> registry) {
        this.recipeRegistry = registry;
        this.recipesByOutput.clear();
        this.knownRecipes.forEach((id, output) -> recipesByOutput.computeIfAbsent(output.getItem(), k -> new ArrayList<>()).add(registry.apply(id)));
    }

    @Override
    public void track(RegistryKey<Recipe<?>> from, ItemStack to) {
        knownRecipes.put(from, to);
        recipesByOutput.computeIfAbsent(to.getItem(), k -> new ArrayList<>()).add(recipeRegistry.apply(from));
        this.markDirty();
    }

    @Override
    public Map<RegistryKey<Recipe<?>>, ItemStack> known() {
        return knownRecipes;
    }

    @Override
    public void reset() {
        knownRecipes.clear();
        recipesByOutput.clear();
        this.markDirty();
    }

    public List<RecipeEntry<?>> getRecipesFor(Item item) {
        return recipesByOutput.getOrDefault(item, List.of());
    }

    public List<Item> getAllOutputs() {
        return new ArrayList<>(recipesByOutput.keySet());
    }

    public boolean knowsRecipesFor(Item item) {
        return recipesByOutput.containsKey(item);
    }
}
