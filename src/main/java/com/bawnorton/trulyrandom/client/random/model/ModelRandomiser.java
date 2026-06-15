package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.random.ClientRandomiserModule;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.Minecraft;

public abstract class ModelRandomiser<T> extends ClientRandomiserModule {
    public abstract void randomise(Minecraft minecraft, long seed);

    public abstract void reset(Minecraft minecraft);

    public abstract void reloadModels(Minecraft minecraft);

    public abstract UnaryMap<T> getRedirectMap(Minecraft minecraft);
}
