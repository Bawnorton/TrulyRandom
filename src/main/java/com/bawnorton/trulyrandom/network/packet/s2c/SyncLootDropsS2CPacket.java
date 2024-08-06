package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.loot.LootTable;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import java.util.HashMap;
import java.util.Map;

public record SyncLootDropsS2CPacket(Map<RegistryKey<LootTable>, LootTableDrops> dropsMap) implements CustomPayload {
    public static final Id<SyncLootDropsS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("synclootdropss2c_s2c"));
    public static final PacketCodec<RegistryByteBuf, SyncLootDropsS2CPacket> PACKET_CODEC = PacketCodecs.map(HashMap::new, RegistryKey.createPacketCodec(RegistryKeys.LOOT_TABLE), LootTableDrops.PACKET_CODEC)
            .xmap(SyncLootDropsS2CPacket::new, packet -> new HashMap<>(packet.dropsMap()));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
