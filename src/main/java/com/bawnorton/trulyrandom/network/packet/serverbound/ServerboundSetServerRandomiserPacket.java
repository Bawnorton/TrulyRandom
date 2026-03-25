package com.bawnorton.trulyrandom.network.packet.serverbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundSetServerRandomiserPacket(Modules modules) implements CustomPacketPayload {
    public static final Type<ServerboundSetServerRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("setserverrandomiser_c2s"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetServerRandomiserPacket> STREAM_CODEC = Modules.STREAM_CODEC.map(ServerboundSetServerRandomiserPacket::new, ServerboundSetServerRandomiserPacket::modules);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
