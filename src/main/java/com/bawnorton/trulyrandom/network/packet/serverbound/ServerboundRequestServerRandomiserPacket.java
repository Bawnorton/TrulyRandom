package com.bawnorton.trulyrandom.network.packet.serverbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundRequestServerRandomiserPacket() implements CustomPacketPayload {
    public static final ServerboundRequestServerRandomiserPacket INSTANCE = new ServerboundRequestServerRandomiserPacket();
    public static final Type<ServerboundRequestServerRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("requestserverrandomiser_c2s"));
    public static final StreamCodec<ByteBuf, ServerboundRequestServerRandomiserPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
