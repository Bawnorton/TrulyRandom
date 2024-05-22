package com.bawnorton.trulyrandom.random.module;

import com.bawnorton.trulyrandom.tracker.Tracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import java.util.List;

public abstract class ServerRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);

    public abstract void reset(MinecraftServer server);

    public abstract Tracker<?, ?> getTracker(PlayerEntity player);

    public abstract List<? extends Tracker<?, ?>> getTrackers();
}
