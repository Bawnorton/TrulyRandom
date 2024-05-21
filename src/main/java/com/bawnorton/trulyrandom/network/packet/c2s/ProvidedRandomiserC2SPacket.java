package com.bawnorton.trulyrandom.network.packet.c2s;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

public record ProvidedRandomiserC2SPacket(Modules modules, UUID requestee) implements CustomPayload {
    public static final Id<ProvidedRandomiserC2SPacket> PACKET_ID = new Id<>(TrulyRandom.id("providedrandomiser_c2s"));
    public static final PacketCodec<ByteBuf, ProvidedRandomiserC2SPacket> PACKET_CODEC = PacketCodec.tuple(
            Modules.PACKET_CODEC, ProvidedRandomiserC2SPacket::modules,
            Uuids.PACKET_CODEC, ProvidedRandomiserC2SPacket::requestee,
            ProvidedRandomiserC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}