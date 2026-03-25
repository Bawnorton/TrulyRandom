package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record ClientboundOpenTargetedRandomiserScreenPacket(UUID target, Modules modules) implements CustomPacketPayload {
    public static final Type<ClientboundOpenTargetedRandomiserScreenPacket> TYPE = new Type<>(TrulyRandom.id("opentargetedrandomiserscreen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenTargetedRandomiserScreenPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientboundOpenTargetedRandomiserScreenPacket::target,
            Modules.STREAM_CODEC, ClientboundOpenTargetedRandomiserScreenPacket::modules,
            ClientboundOpenTargetedRandomiserScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
