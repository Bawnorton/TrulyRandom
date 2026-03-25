package com.bawnorton.trulyrandom.client.random;

import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import net.minecraft.client.MinecraftClient;

public abstract class ClientRandomiserModule extends RandomiserModule {
    public abstract void randomise(MinecraftClient client, long seed);

    public abstract void reset(MinecraftClient client);
}
