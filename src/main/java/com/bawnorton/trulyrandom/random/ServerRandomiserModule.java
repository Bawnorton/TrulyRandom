package com.bawnorton.trulyrandom.random;

import net.minecraft.server.MinecraftServer;

public abstract class ServerRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftServer server, long seed);
    public abstract void reset(MinecraftServer server);
}
