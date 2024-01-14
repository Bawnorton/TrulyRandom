package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

import java.util.UUID;

public record ProvidedRandomiserC2SPacket(Modules modules, UUID requestee) implements FabricPacket {
    public static final PacketType<ProvidedRandomiserC2SPacket> TYPE = PacketType.create(TrulyRandom.id("providedrandomiser_c2s"), ProvidedRandomiserC2SPacket::new);

    public ProvidedRandomiserC2SPacket(PacketByteBuf buf) {
        this(Modules.fromPacket(buf), buf.readUuid());
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
        buf.writeUuid(requestee);
    }

    @Override
    public PacketType<ProvidedRandomiserC2SPacket> getType() {
        return TYPE;
    }
}