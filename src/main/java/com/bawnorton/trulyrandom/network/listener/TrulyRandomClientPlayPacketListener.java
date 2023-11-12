package com.bawnorton.trulyrandom.network.listener;

import com.bawnorton.trulyrandom.network.packet.ShuffleModelsS2CPacket;
import net.minecraft.network.listener.ClientPlayPacketListener;

public interface TrulyRandomClientPlayPacketListener extends ClientPlayPacketListener {
    void trulyRandom$onShuffleModels(ShuffleModelsS2CPacket packet);
}
