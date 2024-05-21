package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record HandshakeC2SPacket() implements CustomPayload {
    public static final HandshakeC2SPacket INSTANCE = new HandshakeC2SPacket();
    public static final Id<HandshakeC2SPacket> PACKET_ID = new Id<>(TrulyRandom.id("handshake_c2s"));
    public static final PacketCodec<ByteBuf, HandshakeC2SPacket> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
