package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.serverbound.ServerboundProvidedRandomiserPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.UUID;

/**
 * Sent to this client from requestee to send this client's randomiser to the requestee.
 * @see ServerboundProvidedRandomiserPacket
 * @see ClientboundOpenTargetedRandomiserScreenPacket
 */
public record ClientboundRequestOtherClientRandomiserPacket(UUID requestee) implements CustomPacketPayload {
    public static final Type<ClientboundRequestOtherClientRandomiserPacket> TYPE = new Type<>(TrulyRandom.id("requestotherclientrandomiser_s2c"));
    public static final StreamCodec<ByteBuf, ClientboundRequestOtherClientRandomiserPacket> STREAM_CODEC = UUIDUtil.STREAM_CODEC.map(ClientboundRequestOtherClientRandomiserPacket::new, ClientboundRequestOtherClientRandomiserPacket::requestee);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
