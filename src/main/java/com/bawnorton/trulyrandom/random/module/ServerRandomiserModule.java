package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.team.Team;
import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class ServerRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);

    public abstract void reset(MinecraftServer server);

    public abstract Tracker<?, ?> getTracker(Team team);

    public abstract Map<UUID, ? extends Tracker<?, ?>> getTrackers();

    public abstract List<? extends Tracker<?, ?>> getTrackerList();
}
