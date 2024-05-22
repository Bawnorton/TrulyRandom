package com.bawnorton.trulyrandom.tracker;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.util.Uuids;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecipeTracker extends Tracker<RecipeEntry<?>, ItemStack> {
    public static final PacketCodec<RegistryByteBuf, RecipeTracker> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(
                    HashMap::new,
                    RecipeEntry.PACKET_CODEC,
                    ItemStack.PACKET_CODEC
            ), tracker -> tracker.knownRecipes,
            Uuids.PACKET_CODEC, tracker -> tracker.playerId,
            RecipeTracker::new
    );

    private final Map<RecipeEntry<?>, ItemStack> knownRecipes;

    public RecipeTracker(Map<RecipeEntry<?>, ItemStack> map, UUID playerId) {
        super(null);
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
    public Iterable<Map.Entry<RecipeEntry<?>, ItemStack>> known() {
        return knownRecipes.entrySet();
    }

    @Override
    public void reset() {
        knownRecipes.clear();
        this.markDirty();
    }
}
