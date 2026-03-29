package com.bawnorton.trulyrandom.client.random;

import com.bawnorton.trulyrandom.random.module.RandomiserModule;
import net.minecraft.client.Minecraft;

public abstract class ClientRandomiserModule extends RandomiserModule {
    public abstract void randomise(Minecraft minecraft, long seed);

    public abstract void reset(Minecraft minecraft);
}
