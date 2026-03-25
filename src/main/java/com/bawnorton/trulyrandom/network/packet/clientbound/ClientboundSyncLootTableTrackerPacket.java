package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ClientboundSyncLootTableTrackerPacket extends ClientboundSyncTrackerPacket<LootTableTracker> {
    public static final Type<ClientboundSyncLootTableTrackerPacket> TYPE = new Type<>(TrulyRandom.id("syncloottabletracker_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncLootTableTrackerPacket> STREAM_CODEC = LootTableTracker.STREAM_CODEC.map(ClientboundSyncLootTableTrackerPacket::new, ClientboundSyncTrackerPacket::tracker).cast();
    
    public ClientboundSyncLootTableTrackerPacket(LootTableTracker tracker) {
        super(tracker);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
