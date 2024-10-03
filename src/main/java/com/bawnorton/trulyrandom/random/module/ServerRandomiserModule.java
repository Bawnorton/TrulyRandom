package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import java.util.List;

public abstract class ServerRandomiserModule<S, R> extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);

    public abstract void reset(MinecraftServer server);

    public abstract Tracker<S, R> getTracker(TeamMember teamMember);

    public abstract List<? extends Tracker<S, R>> getTrackers();

    public abstract List<S> getSources(R result);

    public void initTracker(PlayerEntity player) {
        getTrackers().stream()
                .map(Tracker::getTeam)
                .filter(team -> team != null && team.getOwnerAndPlayers().contains(player.getUuid()))
                .findFirst()
                .ifPresent(player::trulyrandom$joinTeam);
    }
}
