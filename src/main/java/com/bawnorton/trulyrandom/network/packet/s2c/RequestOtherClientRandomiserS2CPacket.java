package com.bawnorton.trulyrandom.network.packet.s2c;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.network.packet.c2s.ProvidedRandomiserC2SPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import java.util.UUID;

/**
 * Sent to this client from requestee to send this client's randomiser to the requestee.
 * @see ProvidedRandomiserC2SPacket
 * @see OpenTargetedRandomiserScreenS2CPacket
 */
public record RequestOtherClientRandomiserS2CPacket(UUID requestee) implements CustomPayload {
    public static final Id<RequestOtherClientRandomiserS2CPacket> PACKET_ID = new Id<>(TrulyRandom.id("requestotherclientrandomiser_s2c"));
    public static final PacketCodec<ByteBuf, RequestOtherClientRandomiserS2CPacket> PACKET_CODEC = Uuids.PACKET_CODEC.xmap(RequestOtherClientRandomiserS2CPacket::new, RequestOtherClientRandomiserS2CPacket::requestee);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
