package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;

public class ItemModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(MinecraftClient client, long seed) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getItemRenderer().getModels();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getOriginalRandomisedMap());
        setRandomised(true);
        reloadModels(client);
    }

    @Override
    public void reset(MinecraftClient client) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getItemRenderer().getModels();
        modelShuffler.trulyrandom$resetModels();
        setRandomised(false);
        reloadModels(client);
    }

    public void reloadModels(MinecraftClient client) {
        ItemModels models = client.getItemRenderer().getModels();
        if (models != null && ((MinecraftClientExtender) client).trulyrandom$isFinishedLoading()) models.reloadModels();
    }

    @Override
    public Module getModule() {
        return Module.ITEM_MODELS;
    }
}
