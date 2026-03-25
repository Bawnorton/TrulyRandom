package com.bawnorton.trulyrandom.random.recipe;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

public record RecipeMetadata(RecipeHolder<?> entry) {
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeMetadata> STREAM_CODEC = StreamCodec.composite(
            RecipeHolder.STREAM_CODEC, RecipeMetadata::entry,
            RecipeMetadata::new
    );

    public Recipe<?> recipe() {
        return entry.value();
    }

    public RecipeType<?> type() {
        return recipe().getType();
    }

    public ResourceKey<Recipe<?>> key() {
        return entry.id();
    }
}
