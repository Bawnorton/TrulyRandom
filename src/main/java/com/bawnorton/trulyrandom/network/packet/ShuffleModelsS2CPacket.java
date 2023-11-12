package com.bawnorton.trulyrandom.network.packet;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record ShuffleModelsS2CPacket(boolean items, boolean blocks) implements Packet<TrulyRandomClientPlayPacketListener> {
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(items);
        buf.writeBoolean(blocks);
    }

    @Override
    public void apply(TrulyRandomClientPlayPacketListener listener) {
        listener.trulyRandom$onShuffleModels(this);
    }
}
