package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import net.minecraft.client.MinecraftClient;

import java.util.Random;

public interface ModelRandomiser {
    default void randomiseModels(MinecraftClient client, long seed) {
        getModelShuffler(client).trulyrandom$shuffleModels(new Random(seed));
        setRandomised(true);
        reloadModels(client);
    }

    default void reset(MinecraftClient client) {
        getModelShuffler(client).trulyrandom$resetModels();
        setRandomised(false);
        reloadModels(client);
    }

    boolean isRandomised();

    void setRandomised(boolean randomised);

    ModelShuffler<?> getModelShuffler(MinecraftClient client);

    void reloadModels(MinecraftClient client);
}
