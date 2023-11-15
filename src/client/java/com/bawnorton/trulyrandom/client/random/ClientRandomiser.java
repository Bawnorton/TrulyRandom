package com.bawnorton.trulyrandom.client.random;

import com.bawnorton.trulyrandom.client.random.model.BlockModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ItemModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ModelRandomiser;
import net.minecraft.client.MinecraftClient;

public class ClientRandomiser {
    private static long localSeed;
    private final ModelRandomiser blockModelRandomiser = new BlockModelRandomiser();
    private final ModelRandomiser itemModelRandomiser = new ItemModelRandomiser();

    public long getLocalSeed() {
        return localSeed;
    }

    public void setLocalSeed(long seed) {
        localSeed = seed;
    }

    public void updateBlockModels(MinecraftClient client, boolean randomise, boolean forceRandomise) {
        updateModels(blockModelRandomiser, client, randomise, forceRandomise);
    }

    public void updateItemModels(MinecraftClient client, boolean randomise, boolean forceRandomise) {
        updateModels(itemModelRandomiser, client, randomise, forceRandomise);
    }

    private void updateModels(ModelRandomiser randomiser, MinecraftClient client, boolean randomise, boolean forceRandomise) {
        if (randomiser.isRandomised() && !randomise) {
            randomiser.reset(client);
        } else if (!randomiser.isRandomised() && randomise || (randomise && forceRandomise)) {
            randomiser.randomiseModels(client, localSeed);
        } else if (randomiser.isRandomised() == randomise) {
            randomiser.reloadModels(client);
        }
    }
}
