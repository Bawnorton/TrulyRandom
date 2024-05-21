package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record OpenRandomiserScreenS2CPacket(Modules modules) implements CustomPayload {
    public static final Id<OpenRandomiserScreenS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("openrandomiserscreen_s2c"));
    public static final PacketCodec<ByteBuf, OpenRandomiserScreenS2CPacket> PACKET_CODEC = Modules.PACKET_CODEC.xmap(OpenRandomiserScreenS2CPacket::new, OpenRandomiserScreenS2CPacket::modules);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
