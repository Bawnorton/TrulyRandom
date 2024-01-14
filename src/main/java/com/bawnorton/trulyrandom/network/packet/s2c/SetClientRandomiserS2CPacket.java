package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record SetClientRandomiserS2CPacket(Modules modules) implements FabricPacket {
    public static final PacketType<SetClientRandomiserS2CPacket> TYPE = PacketType.create(TrulyRandom.id("setclientrandomiser_s2c"), SetClientRandomiserS2CPacket::new);

    public SetClientRandomiserS2CPacket(PacketByteBuf buf) {
        this(Modules.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        modules.write(buf);
    }

    @Override
    public PacketType<SetClientRandomiserS2CPacket> getType() {
        return TYPE;
    }
}
