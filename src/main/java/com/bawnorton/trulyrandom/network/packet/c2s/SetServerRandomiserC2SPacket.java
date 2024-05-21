package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SetServerRandomiserC2SPacket(Modules modules) implements CustomPayload {
    public static final Id<SetServerRandomiserC2SPacket> PACKET_ID = new Id<>(TrulyRandom.id("setserverrandomiser_c2s"));
    public static final PacketCodec<ByteBuf, SetServerRandomiserC2SPacket> PACKET_CODEC = Modules.PACKET_CODEC.xmap(SetServerRandomiserC2SPacket::new, SetServerRandomiserC2SPacket::modules);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
