package com.bawnorton.trulyrandom.network.packet.clientbound;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.team.Team;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundChangeTeamPacket(Team team) implements CustomPacketPayload {
    public static final Type<ClientboundChangeTeamPacket> TYPE = new Type<>(TrulyRandom.id("change_team_s2c"));
    public static final StreamCodec<ByteBuf, ClientboundChangeTeamPacket> STREAM_CODEC = Team.STREAM_CODEC.map(ClientboundChangeTeamPacket::new, ClientboundChangeTeamPacket::team);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
