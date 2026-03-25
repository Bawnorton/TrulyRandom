package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundSetClientRandomiserPacket(Modules modules) implements CustomPacketPayload {
    public static final Type<ClientboundSetClientRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("setclientrandomiser_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetClientRandomiserPacket> STREAM_CODEC = Modules.STREAM_CODEC.map(ClientboundSetClientRandomiserPacket::new, ClientboundSetClientRandomiserPacket::modules);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
