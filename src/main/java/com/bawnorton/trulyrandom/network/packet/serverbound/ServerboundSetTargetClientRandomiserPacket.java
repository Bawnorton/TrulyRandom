package com.bawnorton.trulyrandom.network.packet.serverbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ServerboundSetTargetClientRandomiserPacket(Modules modules, UUID target) implements CustomPacketPayload {
    public static final Type<ServerboundSetTargetClientRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("settargetclientrandomiser_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetTargetClientRandomiserPacket> STREAM_CODEC = StreamCodec.composite(
            Modules.STREAM_CODEC, ServerboundSetTargetClientRandomiserPacket::modules,
            UUIDUtil.STREAM_CODEC, ServerboundSetTargetClientRandomiserPacket::target,
            ServerboundSetTargetClientRandomiserPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
