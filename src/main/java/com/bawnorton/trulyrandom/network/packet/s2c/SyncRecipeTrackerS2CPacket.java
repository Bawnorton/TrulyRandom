package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class SyncRecipeTrackerS2CPacket extends SyncTrackerS2CPacket<RecipeTracker> {
    public static final Id<SyncRecipeTrackerS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("syncrecipetracker_s2c"));
    public static final PacketCodec<RegistryByteBuf, SyncRecipeTrackerS2CPacket> PACKET_CODEC = RecipeTracker.PACKET_CODEC.xmap(SyncRecipeTrackerS2CPacket::new, SyncTrackerS2CPacket::tracker).cast();

    public SyncRecipeTrackerS2CPacket(RecipeTracker tracker) {
        super(tracker);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
