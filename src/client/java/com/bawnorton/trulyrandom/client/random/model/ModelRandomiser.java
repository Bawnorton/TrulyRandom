package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.random.ClientRandomiserModule;
import net.minecraft.client.MinecraftClient;

public abstract class ModelRandomiser extends ClientRandomiserModule {
    public void randomise(MinecraftClient client, long seed) {
        getModelShuffler(client).trulyrandom$shuffleModels(seed);
        setRandomised(true);
        reloadModels(client);
    }

    public void reset(MinecraftClient client) {
        getModelShuffler(client).trulyrandom$resetModels();
        setRandomised(false);
        reloadModels(client);
    }

    protected abstract ModelShuffler<?> getModelShuffler(MinecraftClient client);

    public abstract void reloadModels(MinecraftClient client);
}
