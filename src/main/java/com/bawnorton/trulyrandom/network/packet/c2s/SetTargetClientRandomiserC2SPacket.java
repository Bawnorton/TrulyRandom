package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public record SetTargetClientRandomiserC2SPacket(Modules modules, UUID target) implements FabricPacket {
    public static final PacketType<SetTargetClientRandomiserC2SPacket> TYPE = PacketType.create(TrulyRandom.id("settargetclientrandomiser_c2s"), SetTargetClientRandomiserC2SPacket::new);

    public SetTargetClientRandomiserC2SPacket(PacketByteBuf buf) {
        this(Modules.fromPacket(buf), buf.readUuid());
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
        buf.writeUuid(target);
    }

    @Override
    public PacketType<SetTargetClientRandomiserC2SPacket> getType() {
        return TYPE;
    }
}
