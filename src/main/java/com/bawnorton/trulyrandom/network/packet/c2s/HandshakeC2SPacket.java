package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record HandshakeC2SPacket() implements FabricPacket {
    public static final PacketType<HandshakeC2SPacket> TYPE = PacketType.create(TrulyRandom.id("handshake_c2s"), HandshakeC2SPacket::new);

    public HandshakeC2SPacket(PacketByteBuf buf) {
        this();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public PacketType<HandshakeC2SPacket> getType() {
        return TYPE;
    }
}
