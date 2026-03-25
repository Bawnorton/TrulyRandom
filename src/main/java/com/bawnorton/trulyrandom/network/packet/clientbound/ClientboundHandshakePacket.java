package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import io.netty.buffer.ByteBuf;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundHandshakePacket(String versionString) implements CustomPacketPayload {
    public static final Type<ClientboundHandshakePacket> TYPE = new Type<>(TrulyRandom.id("handshake_s2c"));
    public static final StreamCodec<ByteBuf, ClientboundHandshakePacket> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ClientboundHandshakePacket::new, ClientboundHandshakePacket::versionString);

    public ClientboundHandshakePacket(Version version) {
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
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
