package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomServerPlayPacketListener;
import com.bawnorton.trulyrandom.random.Modules;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record SyncRandomiserC2SPacket(Modules modules,
                                      long seed) implements Packet<TrulyRandomServerPlayPacketListener> {
    public SyncRandomiserC2SPacket(PacketByteBuf buf) {
        this(Modules.fromPacket(buf), buf.readLong());
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
        buf.writeLong(seed);
    }

    @Override
    public void apply(TrulyRandomServerPlayPacketListener listener) {
        listener.trulyRandom$onSyncRandomiser(this);
    }
}
