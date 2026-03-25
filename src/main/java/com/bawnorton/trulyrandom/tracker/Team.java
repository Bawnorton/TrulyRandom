package com.bawnorton.trulyrandom.tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Team {
    public static final Codec<Team> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter(Team::getOwner),
            Codec.list(UUIDUtil.CODEC).fieldOf("players").forGetter(Team::getPlayers)
    ).apply(instance, Team::new));

    public static final StreamCodec<ByteBuf, Team> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Team::getOwner,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Team::getPlayers,
            Team::new
    );

    private UUID owner;
    private final List<UUID> players;

    private Team(UUID owner) {
        this.owner = owner;
        this.players = new ArrayList<>();
    }

    private Team(UUID owner, List<UUID> players) {
        this.owner = owner;
        this.players = players;
    }

    public static Team create(UUID owner) {
        return new Team(owner);
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isOwner(UUID player) {
        return owner.equals(player);
    }

    public void transferOwnership(UUID player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (isOwner(player)) return;
        if (!players.contains(player)) {
            throw new IllegalArgumentException("Player is not a member of the team");
        }

        owner = player;
        players.remove(player);
    }

    public void addPlayer(UUID player) {
        players.add(player);
    }

    public void removePlayer(UUID player) {
        if (isOwner(player)) {
            if(!players.isEmpty()) {
                transferOwnership(players.getFirst());
            }
        } else {
            players.remove(player);
        }
    }

    public boolean belongsToTeam(UUID player) {
        return owner.equals(player) || players.contains(player);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getOwnerAndPlayers() {
        List<UUID> result = new ArrayList<>();
        result.add(owner);
        result.addAll(players);
        return result;
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team team) {
            return owner.equals(team.owner) && players.equals(team.players);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return owner.hashCode() ^ players.hashCode();
    }
}
