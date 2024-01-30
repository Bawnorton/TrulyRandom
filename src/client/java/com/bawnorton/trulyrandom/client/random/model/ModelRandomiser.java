package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.random.ClientRandomiserModule;
import net.minecraft.client.MinecraftClient;

public abstract class ModelRandomiser extends ClientRandomiserModule {
    public abstract void randomise(MinecraftClient client, long seed);

    public abstract void reset(MinecraftClient client);

    public abstract void reloadModels(MinecraftClient client);
}
