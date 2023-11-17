package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.network.listener.TrulyRandomClientPlayPacketListener;
import com.bawnorton.trulyrandom.random.Modules;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;

public class HandshakeS2CPacket implements Packet<TrulyRandomClientPlayPacketListener> {
    private final Version version;
    private final Modules modules;
    private final long seed;

    public HandshakeS2CPacket(PacketByteBuf packetByteBuf) {
        try {
            version = Version.parse(packetByteBuf.readString());
            modules = Modules.fromPacket(packetByteBuf);
            seed = packetByteBuf.readLong();
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    public HandshakeS2CPacket(Version version, Modules modules, long seed) {
        this.version = version;
        this.modules = modules;
        this.seed = seed;
    }

    public Version version() {
        return version;
    }

    public Modules modules() {
        return modules;
    }

    public long seed() {
        return seed;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(version.getFriendlyString());
        modules.write(buf);
        buf.writeLong(seed);
    }

    @Override
    public void apply(TrulyRandomClientPlayPacketListener listener) {
        listener.trulyRandom$onHandshake(this);
    }
}
