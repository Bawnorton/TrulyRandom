package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundOpenRandomiserScreenPacket(Modules modules) implements CustomPacketPayload {
    public static final Type<ClientboundOpenRandomiserScreenPacket> TYPE = new Type<>(TrulyRandom.id("openrandomiserscreen_s2c"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenRandomiserScreenPacket> STREAM_CODEC = Modules.STREAM_CODEC.map(ClientboundOpenRandomiserScreenPacket::new, ClientboundOpenRandomiserScreenPacket::modules);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
