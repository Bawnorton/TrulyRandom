package com.bawnorton.trulyrandom.tracker.recipe;

import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import java.util.HashMap;
import java.util.Map;

public class RecipeTracker extends Tracker<RecipeEntry<?>, ItemStack> {
    public static final PacketCodec<RegistryByteBuf, RecipeTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    HashMap::new,
                    RecipeEntry.PACKET_CODEC,
                    ItemStack.PACKET_CODEC
            ), tracker -> tracker.knownRecipes,
            Team.PACKET_CODEC, tracker -> tracker.team,
            RecipeTracker::new
    );

    private final Map<RecipeEntry<?>, ItemStack> knownRecipes;

    public RecipeTracker(Map<RecipeEntry<?>, ItemStack> map, Team team) {
        super(team);
        this.knownRecipes = new HashMap<>();
    }

    public RecipeTracker() {
        super(null);
        this.knownRecipes = new HashMap<>();
    }

    @Override
    public void track(RecipeEntry<?> from, ItemStack to) {
        knownRecipes.put(from, to);
        this.markDirty();
    }

    @Override
    public Map<RecipeEntry<?>, ItemStack> known() {
        return knownRecipes;
    }

    @Override
    public void reset() {
        knownRecipes.clear();
        this.markDirty();
    }
}
