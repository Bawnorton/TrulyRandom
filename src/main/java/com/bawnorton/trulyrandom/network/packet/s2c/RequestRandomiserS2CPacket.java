package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public record RequestRandomiserS2CPacket(UUID requestee) implements FabricPacket {
    public static final PacketType<RequestRandomiserS2CPacket> TYPE = PacketType.create(TrulyRandom.id("requestrandomiser_s2c"), RequestRandomiserS2CPacket::new);

    public RequestRandomiserS2CPacket(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readUuid());
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(requestee);
    }

    @Override
    public PacketType<RequestRandomiserS2CPacket> getType() {
        return TYPE;
    }
}
