package com.bawnorton.trulyrandom.network.listener;

import com.bawnorton.trulyrandom.network.packet.c2s.HandshakeC2SPacket;
import com.bawnorton.trulyrandom.network.packet.c2s.SyncRandomiserC2SPacket;
import net.minecraft.network.listener.ServerPlayPacketListener;

public interface TrulyRandomServerPlayPacketListener extends ServerPlayPacketListener {
    void trulyRandom$onSyncRandomiser(SyncRandomiserC2SPacket packet);

    void trulyRandom$onHandshake(HandshakeC2SPacket packet);
}
