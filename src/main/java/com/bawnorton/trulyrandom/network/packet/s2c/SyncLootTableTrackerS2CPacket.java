package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class SyncLootTableTrackerS2CPacket extends SyncTrackerS2CPacket<LootTableTracker> {
    public static final Id<SyncLootTableTrackerS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("syncloottabletracker_s2c"));
    public static final PacketCodec<RegistryByteBuf, SyncLootTableTrackerS2CPacket> PACKET_CODEC = LootTableTracker.PACKET_CODEC.xmap(SyncLootTableTrackerS2CPacket::new, SyncTrackerS2CPacket::tracker).cast();
    
    public SyncLootTableTrackerS2CPacket(LootTableTracker tracker) {
        super(tracker);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
