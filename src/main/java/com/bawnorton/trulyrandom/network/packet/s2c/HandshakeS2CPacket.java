package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import com.bawnorton.trulyrandom.random.Modules;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public record HandshakeS2CPacket(String versionString, Modules modules, long seed) implements Packet<TrulyRandomClientPlayPacketListener> {
    public HandshakeS2CPacket(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readString(), Modules.fromPacket(packetByteBuf), packetByteBuf.readLong());
    }

    public HandshakeS2CPacket(Version version, Modules modules, long seed) {
        this(version.getFriendlyString(), modules, seed);
    }

    public Version version() {
        try {
            return Version.parse(versionString);
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(versionString);
        modules.write(buf);
        buf.writeLong(seed);
    }

    @Override
    public void apply(TrulyRandomClientPlayPacketListener listener) {
        listener.trulyRandom$onHandshake(this);
    }
}
