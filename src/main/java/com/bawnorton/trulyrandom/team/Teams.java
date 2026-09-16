package com.bawnorton.trulyrandom.team;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class Teams implements Iterable<Team> {
    public static final Codec<Teams> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, Team.CODEC).xmap(Teams::new, teams -> teams.teams);

    private final Map<UUID, Team> teams;

    private Teams(Map<UUID, Team> teams) {
        this.teams = new HashMap<>(teams);
    }

    public static Teams create() {
        return new Teams(new HashMap<>());
    }

    public Team getOrCreate(UUID owner) {
        return teams.computeIfAbsent(owner, Team::create);
    }

    public void removeMembership(Player player) {
        teams.remove(player.getUUID());
        player.trulyrandom$setTeam(null);
    }

    public void addMembership(Player player, Team team) {
        teams.put(player.getUUID(), team);
        player.trulyrandom$setTeam(team);
    }

    public Optional<Team> findTeamIBelongTo(UUID player) {
        for (Map.Entry<UUID, Team> entry : teams.entrySet()) {
            if (entry.getValue().isMemberOrOwner(player)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Iterator<Team> iterator() {
        return teams.values().iterator();
    }
}
