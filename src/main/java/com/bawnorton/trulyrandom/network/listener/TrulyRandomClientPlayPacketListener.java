package com.bawnorton.trulyrandom.network.listener;

import com.bawnorton.trulyrandom.network.packet.s2c.HandshakeS2CPacket;
import com.bawnorton.trulyrandom.network.packet.s2c.ShuffleModelsS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;

public interface TrulyRandomClientPlayPacketListener extends ClientPlayPacketListener {
    void trulyRandom$onShuffleModels(ShuffleModelsS2CPacket packet);

    void trulyRandom$onHandshake(HandshakeS2CPacket packet);
}
