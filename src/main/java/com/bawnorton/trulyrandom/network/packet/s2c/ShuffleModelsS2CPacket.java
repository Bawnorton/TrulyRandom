package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record ShuffleModelsS2CPacket(boolean items, boolean blocks, boolean seedChanged,
                                     long seed) implements Packet<TrulyRandomClientPlayPacketListener> {
    public ShuffleModelsS2CPacket(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readBoolean(), packetByteBuf.readBoolean(), packetByteBuf.readBoolean(), packetByteBuf.readLong());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(items);
        buf.writeBoolean(blocks);
        buf.writeBoolean(seedChanged);
        buf.writeLong(seed);
    }

    @Override
    public void apply(TrulyRandomClientPlayPacketListener listener) {
        listener.trulyRandom$onShuffleModels(this);
    }
}
