package com.bawnorton.trulyrandom.tracker.recipe;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import com.bawnorton.trulyrandom.util.collection.UnaryBiMap;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import java.util.HashMap;
import java.util.Map;

public class RecipeTracker extends Tracker<RegistryKey<Recipe<?>>, RegistryKey<Recipe<?>>> {
    public static final PacketCodec<RegistryByteBuf, RecipeTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    UnaryHashMap::new,
                    RegistryKey.createPacketCodec(RegistryKeys.RECIPE),
                    RegistryKey.createPacketCodec(RegistryKeys.RECIPE)
            ), tracker -> tracker.knownRecipes,
            Team.PACKET_CODEC, tracker -> tracker.team,
            RecipeTracker::new
    );

    private final UnaryMap<RegistryKey<Recipe<?>>> knownRecipes;

    public RecipeTracker(UnaryMap<RegistryKey<Recipe<?>>> map, Team team) {
        super(team);
        this.knownRecipes = new UnaryHashMap<>(map);
    }

    public RecipeTracker() {
        super(null);
        this.knownRecipes = new UnaryHashMap<>();
    }

    @Override
    public void track(RegistryKey<Recipe<?>> from, RegistryKey<Recipe<?>> to) {
        knownRecipes.put(from, to);
        this.markDirty();
    }

    @Override
    public UnaryMap<RegistryKey<Recipe<?>>> known() {
        return knownRecipes;
    }

    @Override
    public void reset() {
        knownRecipes.clear();
        this.markDirty();
    }
}
