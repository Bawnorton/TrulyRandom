package com.bawnorton.trulyrandom.random.recipe;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryKey;

public record RecipeMetadata(RecipeEntry<?> entry) {
    public static final PacketCodec<RegistryByteBuf, RecipeMetadata> PACKET_CODEC = PacketCodec.tuple(
            RecipeEntry.PACKET_CODEC, RecipeMetadata::entry,
            RecipeMetadata::new
    );

    public Recipe<?> recipe() {
        return entry.value();
    }

    public RecipeType<?> type() {
        return recipe().getType();
    }

    public RegistryKey<Recipe<?>> key() {
        return entry.id();
    }
}
