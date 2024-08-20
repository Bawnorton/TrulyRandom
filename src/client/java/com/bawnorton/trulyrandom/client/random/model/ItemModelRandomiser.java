package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.item.Item;

public class ItemModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(MinecraftClient client, long seed) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getItemRenderer().getModels();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getRedirectMap());
    }

    @Override
    public void reset(MinecraftClient client) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getItemRenderer().getModels();
        modelShuffler.trulyrandom$resetModels();
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

    public void updateItemModels(UnaryMap<Item> redirectMap) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) MinecraftClient.getInstance().getItemRenderer().getModels();
        modelShuffler.trulyrandom$updateModels(redirectMap);
    }
}
