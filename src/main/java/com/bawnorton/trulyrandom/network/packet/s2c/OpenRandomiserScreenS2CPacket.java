package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record OpenRandomiserScreenS2CPacket(Modules modules) implements FabricPacket {
    public static final PacketType<OpenRandomiserScreenS2CPacket> TYPE = PacketType.create(TrulyRandom.id("openrandomiserscreen_s2c"), OpenRandomiserScreenS2CPacket::new);

    public OpenRandomiserScreenS2CPacket(PacketByteBuf packetByteBuf) {
        this(Modules.fromPacket(packetByteBuf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
    }

    @Override
    public PacketType<OpenRandomiserScreenS2CPacket> getType() {
        return TYPE;
    }
}
