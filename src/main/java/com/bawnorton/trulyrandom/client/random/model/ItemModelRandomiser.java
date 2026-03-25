package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;

public class ItemModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(MinecraftClient client, long seed) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getBakedModelManager();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getRedirectMap());
    }

    @Override
    public void reset(MinecraftClient client) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) client.getBakedModelManager();
        modelShuffler.trulyrandom$resetModels();
        reloadModels(client);
    }

    public void reloadModels(MinecraftClient client) {
        BakedModelManager bakedModelManager = client.getBakedModelManager();
//        if (bakedModelManager != null && ((MinecraftClientExtender) client).trulyrandom$isFinishedLoading()) bakedModelManager.reloadModels();
    }

    @Override
    public Module getModule() {
        return Module.ITEM_MODELS;
    }

    public void updateItemModels(UnaryMap<Identifier> redirectMap) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) MinecraftClient.getInstance().getBakedModelManager();
        modelShuffler.trulyrandom$updateModels(redirectMap);
    }
}
