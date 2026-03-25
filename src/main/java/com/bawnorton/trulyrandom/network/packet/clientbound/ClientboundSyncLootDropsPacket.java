package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.tracker.loot.drop.LootTableDrops;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.Map;

public record ClientboundSyncLootDropsPacket(Map<ResourceKey<LootTable>, LootTableDrops> dropsMap) implements CustomPacketPayload {
    public static final Type<ClientboundSyncLootDropsPacket> TYPE = new Type<>(TrulyRandom.id("synclootdropss2c_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncLootDropsPacket> STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ResourceKey.streamCodec(Registries.LOOT_TABLE), LootTableDrops.STREAM_CODEC)
            .map(ClientboundSyncLootDropsPacket::new, packet -> new HashMap<>(packet.dropsMap()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
