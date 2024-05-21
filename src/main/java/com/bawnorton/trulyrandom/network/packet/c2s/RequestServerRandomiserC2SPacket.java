package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

public record RequestServerRandomiserC2SPacket() implements CustomPayload {
    public static final RequestServerRandomiserC2SPacket INSTANCE = new RequestServerRandomiserC2SPacket();
    public static final Id<RequestServerRandomiserC2SPacket> PACKET_ID = new Id<>(TrulyRandom.id("requestserverrandomiser_c2s"));
    public static final PacketCodec<ByteBuf, RequestServerRandomiserC2SPacket> PACKET_CODEC = PacketCodec.unit(INSTANCE);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
