package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record HandshakeS2CPacket(String versionString) implements CustomPayload {
    public static final Id<HandshakeS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("handshake_s2c"));
    public static final PacketCodec<ByteBuf, HandshakeS2CPacket> PACKET_CODEC = PacketCodecs.STRING.xmap(HandshakeS2CPacket::new, HandshakeS2CPacket::versionString);

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
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
