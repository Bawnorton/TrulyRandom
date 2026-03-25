package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundSyncRecipeTrackerPacket extends ClientboundSyncTrackerPacket<RecipeTracker> {
    public static final Type<ClientboundSyncRecipeTrackerPacket> TYPE = new Type<>(TrulyRandom.id("syncrecipetracker_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncRecipeTrackerPacket> STREAM_CODEC = RecipeTracker.STREAM_CODEC.map(ClientboundSyncRecipeTrackerPacket::new, ClientboundSyncTrackerPacket::tracker).cast();

    public ClientboundSyncRecipeTrackerPacket(RecipeTracker tracker) {
        super(tracker);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
