package com.bawnorton.trulyrandom.client.random;

import com.bawnorton.trulyrandom.client.random.model.BlockModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ItemModelRandomiser;
import com.bawnorton.trulyrandom.client.random.model.ModelRandomiser;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ClientRandomiser extends Randomiser {
    public static final ClientRandomiser DEFAULT = new ClientRandomiser();
    private final ModelRandomiser blockModelRandomiser = new BlockModelRandomiser();
    private final ModelRandomiser itemModelRandomiser = new ItemModelRandomiser();

    private ClientRandomiser(@NotNull Modules modules) {
        super(modules);
    }

    private ClientRandomiser() {
        this(new Modules());
    }

    public void updateBlockModels(MinecraftClient client, boolean seedChanged) {
        update(blockModelRandomiser, client, modules.isEnabled(blockModelRandomiser.getModule()), seedChanged);
    }

    public void updateItemModels(MinecraftClient client, boolean seedChanged) {
        update(itemModelRandomiser, client, modules.isEnabled(itemModelRandomiser.getModule()), seedChanged);
    }

    private void update(ModelRandomiser randomiser, MinecraftClient client, boolean randomise, boolean forceRandomise) {
        if (randomiser.isRandomised() && !randomise) {
            randomiser.reset(client);
        } else if (!randomiser.isRandomised() && randomise || (randomise && forceRandomise)) {
            randomiser.randomise(client, modules.getSeed(randomiser.getModule()));
        } else if (randomiser.isRandomised() == randomise) {
            randomiser.reloadModels(client);
        }
    }
}
