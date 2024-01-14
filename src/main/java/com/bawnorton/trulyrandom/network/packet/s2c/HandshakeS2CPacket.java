package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.PacketByteBuf;

public record HandshakeS2CPacket(String versionString) implements FabricPacket {
    public static final PacketType<HandshakeS2CPacket> TYPE = PacketType.create(TrulyRandom.id("handshake_s2c"), HandshakeS2CPacket::new);

    public HandshakeS2CPacket(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readString());
    }

    public HandshakeS2CPacket(Version version) {
        this(version.getFriendlyString());
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
    }

    @Override
    public PacketType<HandshakeS2CPacket> getType() {
        return TYPE;
    }
}
