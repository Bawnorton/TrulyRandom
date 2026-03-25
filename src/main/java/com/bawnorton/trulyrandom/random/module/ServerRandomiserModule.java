package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Map;

public abstract class ServerRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);

    public abstract void reset(MinecraftServer server);

    public abstract Tracker<?, ?> getTracker(TeamMember teamMember);

    public abstract Map<Team, ? extends Tracker<?, ?>> getTrackers();

    public abstract List<? extends Tracker<?, ?>> getTrackerList();

    public void initTracker(Player player) {
        getTrackerList().stream()
                .map(Tracker::getTeam)
                .filter(team -> team != null && team.getOwnerAndPlayers().contains(player.getUUID()))
                .findFirst()
                .ifPresent(player::trulyrandom$joinTeam);
    }
}
