package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomServerPlayPacketListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public class HandshakeC2SPacket implements Packet<TrulyRandomServerPlayPacketListener> {
    public HandshakeC2SPacket(PacketByteBuf buf) {
    }

    public HandshakeC2SPacket() {
    }

    @Override
    public void write(PacketByteBuf buf) {
    }

    @Override
    public void apply(TrulyRandomServerPlayPacketListener listener) {
        listener.trulyRandom$onHandshake(this);
    }
}
