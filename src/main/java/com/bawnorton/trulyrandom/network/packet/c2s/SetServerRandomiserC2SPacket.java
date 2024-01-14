package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record SetServerRandomiserC2SPacket(Modules modules) implements FabricPacket {
    public static final PacketType<SetServerRandomiserC2SPacket> TYPE = PacketType.create(TrulyRandom.id("setserverrandomiser_c2s"), SetServerRandomiserC2SPacket::new);

    public SetServerRandomiserC2SPacket(PacketByteBuf buf) {
        this(Modules.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
