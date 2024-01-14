package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public record OpenTargetedRandomiserScreenS2CPacket(UUID target, Modules modules) implements FabricPacket {
    public static final PacketType<OpenTargetedRandomiserScreenS2CPacket> TYPE = PacketType.create(TrulyRandom.id("opentargetedrandomiserscreen_s2c"), OpenTargetedRandomiserScreenS2CPacket::new);

    public OpenTargetedRandomiserScreenS2CPacket(PacketByteBuf buf) {
        this(buf.readUuid(), Modules.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeUuid(target);
        modules.write(buf);
    }

    @Override
    public PacketType<OpenTargetedRandomiserScreenS2CPacket> getType() {
        return TYPE;
    }
}
