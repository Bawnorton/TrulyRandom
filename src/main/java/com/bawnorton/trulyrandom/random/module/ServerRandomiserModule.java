package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.List;
import java.util.UUID;

public abstract class ServerRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);

    public abstract void reset(MinecraftServer server);

    public abstract Tracker<?, ?> getTracker(TeamMember teamMember);

    public abstract List<? extends Tracker<?, ?>> getTrackers();

    public void initTracker(PlayerEntity player) {
        getTrackers().stream()
                .map(Tracker::getTeam)
                .filter(team -> team != null && team.getOwnerAndPlayers().contains(player.getUuid()))
                .findFirst()
                .ifPresent(player::trulyrandom$joinTeam);
    }
}
