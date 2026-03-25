package com.bawnorton.trulyrandom.network.packet.serverbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundHandshakePacket() implements CustomPacketPayload {
    public static final ServerboundHandshakePacket INSTANCE = new ServerboundHandshakePacket();
    public static final Type<ServerboundHandshakePacket> TYPE = new Type<>(TrulyRandom.id("handshake_c2s"));
    public static final StreamCodec<ByteBuf, ServerboundHandshakePacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
