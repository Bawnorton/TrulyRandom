package com.bawnorton.trulyrandom.network.packet.serverbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ServerboundProvidedRandomiserPacket(Modules modules, UUID requestee) implements CustomPacketPayload {
    public static final Type<ServerboundProvidedRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("providedrandomiser_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundProvidedRandomiserPacket> STREAM_CODEC = StreamCodec.composite(
            Modules.STREAM_CODEC, ServerboundProvidedRandomiserPacket::modules,
            UUIDUtil.STREAM_CODEC, ServerboundProvidedRandomiserPacket::requestee,
            ServerboundProvidedRandomiserPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}