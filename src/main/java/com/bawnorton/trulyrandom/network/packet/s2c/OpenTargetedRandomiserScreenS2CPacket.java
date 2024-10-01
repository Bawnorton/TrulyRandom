package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

public record OpenTargetedRandomiserScreenS2CPacket(UUID target, Modules modules) implements CustomPayload {
    public static final Id<OpenTargetedRandomiserScreenS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("opentargetedrandomiserscreen_s2c"));
    public static final PacketCodec<RegistryByteBuf, OpenTargetedRandomiserScreenS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            Uuids.PACKET_CODEC, OpenTargetedRandomiserScreenS2CPacket::target,
            Modules.PACKET_CODEC, OpenTargetedRandomiserScreenS2CPacket::modules,
            OpenTargetedRandomiserScreenS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
