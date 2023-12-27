package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomServerPlayPacketListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record HandshakeC2SPacket() implements Packet<TrulyRandomServerPlayPacketListener> {
    public HandshakeC2SPacket(PacketByteBuf buf) {
        this();
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public void apply(TrulyRandomServerPlayPacketListener listener) {
        listener.trulyRandom$onHandshake(this);
    }
}
