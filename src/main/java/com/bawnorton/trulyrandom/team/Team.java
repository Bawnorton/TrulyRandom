package com.bawnorton.trulyrandom.team;

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
            Codec.list(UUIDUtil.CODEC).fieldOf("players").forGetter(Team::getPlayers),
            Codec.list(UUIDUtil.CODEC).fieldOf("banned").forGetter(Team::getBanned),
            Codec.list(UUIDUtil.CODEC).fieldOf("invited").forGetter(Team::getInvited)
    ).apply(instance, Team::new));

    public static final StreamCodec<ByteBuf, Team> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, Team::getOwner,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Team::getPlayers,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Team::getBanned,
            UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), Team::getInvited,
            Team::new
    );

    private UUID owner;
    private final List<UUID> players;
    private final List<UUID> banned;
    private final List<UUID> invited;

    private Team(UUID owner) {
        this.owner = owner;
        this.players = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.invited = new ArrayList<>();
    }

    private Team(UUID owner, List<UUID> players, List<UUID> banned, List<UUID> invited) {
        this.owner = owner;
        this.players = new ArrayList<>(players);
        this.banned = new ArrayList<>(banned);
        this.invited = new ArrayList<>(invited);
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

    public TeamActionResult transferOwnership(UUID player) {
        if (isOwner(player)) return TeamActionResult.IS_ALREADY_OWNER;
        if (!isMember(player)) return TeamActionResult.IS_NOT_A_MEMBER;

        players.add(owner);
        owner = player;
        players.remove(player);
        return TeamActionResult.SUCCESS.withVariant("transfered");
    }

    public TeamActionResult addPlayer(UUID player) {
        if (isBanned(player)) return TeamActionResult.IS_BANNED;
        if (!isInvited(player)) return TeamActionResult.IS_NOT_INVITED;

        players.add(player);
        invited.remove(player);
        return TeamActionResult.SUCCESS.withVariant("added");
    }

    public TeamActionResult kickPlayer(UUID player) {
        if (isOwner(player)) return TeamActionResult.IS_OWNER.asNegative().withVariant("kick");

        players.remove(player);
        return TeamActionResult.SUCCESS.withVariant("kicked");
    }

    public TeamActionResult banPlayer(UUID player) {
        if (isOwner(player)) return TeamActionResult.IS_OWNER.asNegative().withVariant("ban");
        if (isBanned(player)) return TeamActionResult.ALREADY_BANNED;
        if (isInvited(player)) {
            invited.remove(player);
        }

        banned.add(player);
        return kickPlayer(player).withVariant("banned");
    }

    public TeamActionResult unbanPlayer(UUID player) {
        if (!isBanned(player)) return TeamActionResult.IS_NOT_BANNED;

        banned.remove(player);
        return TeamActionResult.SUCCESS.withVariant("unbanned");
    }

    public TeamActionResult invitePlayer(UUID player) {
        if (isBanned(player)) return TeamActionResult.IS_BANNED;
        if (isInvited(player)) return TeamActionResult.ALREADY_INVITED;
        if (isMemberOrOwner(player)) return TeamActionResult.ALREADY_IN_TEAM;

        invited.add(player);
        return TeamActionResult.SUCCESS.withVariant("invited");
    }

    public TeamActionResult uninvitePlayer(UUID player) {
        if (!isInvited(player)) return TeamActionResult.IS_NOT_INVITED;
        if (isMemberOrOwner(player)) return TeamActionResult.ALREADY_IN_TEAM;
        if (isBanned(player)) return TeamActionResult.IS_BANNED;

        invited.remove(player);
        return TeamActionResult.SUCCESS.withVariant("uninvited");
    }

    public boolean isMemberOrOwner(UUID player) {
        return isOwner(player) || isMember(player);
    }

    public boolean isMember(UUID player) {
        return players.contains(player);
    }

    public boolean isBanned(UUID player) {
        return banned.contains(player);
    }

    public boolean isInvited(UUID player) {
        return invited.contains(player);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<UUID> getBanned() {
        return banned;
    }

    public List<UUID> getInvited() {
        return invited;
    }

    public List<UUID> getOwnerAndPlayers() {
        List<UUID> result = new ArrayList<>();
        result.add(owner);
        result.addAll(players);
        return result;
    }

    public boolean hasMembers() {
        return !players.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team team) {
            return owner.equals(team.owner)
                    && players.equals(team.players)
                    && banned.equals(team.banned)
                    && invited.equals(team.invited);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return owner.hashCode() ^ players.hashCode() ^ banned.hashCode() ^ invited.hashCode();
    }
}
