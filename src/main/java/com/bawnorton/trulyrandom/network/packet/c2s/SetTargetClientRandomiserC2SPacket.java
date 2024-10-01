package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

public record SetTargetClientRandomiserC2SPacket(Modules modules, UUID target) implements CustomPayload {
    public static final Id<SetTargetClientRandomiserC2SPacket> PACKET_ID = new Id<>(TrulyRandom.id("settargetclientrandomiser_c2s"));
    public static final PacketCodec<RegistryByteBuf, SetTargetClientRandomiserC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            Modules.PACKET_CODEC, SetTargetClientRandomiserC2SPacket::modules,
            Uuids.PACKET_CODEC, SetTargetClientRandomiserC2SPacket::target,
            SetTargetClientRandomiserC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
