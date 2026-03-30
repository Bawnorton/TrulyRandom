package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;

public class ItemModelRandomiser extends ModelRandomiser {
    @Override
    public void randomise(Minecraft minecraft, long seed) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
        modelShuffler.trulyrandom$shuffleModels(seed);
        ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getRedirectMap());
    }

    @Override
    public void reset(Minecraft minecraft) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
        modelShuffler.trulyrandom$resetModels();
        reloadModels(minecraft);
    }

    public void reloadModels(Minecraft minecraft) {
        ModelManager modelManager = minecraft.getModelManager();
//        modelManager.reload()
    }

    @Override
    public Module getModule() {
        return Module.ITEM_MODELS;
    }

    public void updateItemModels(UnaryMap<Identifier> redirectMap) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) Minecraft.getInstance().getModelManager();
        modelShuffler.trulyrandom$updateModels(redirectMap);
    }
}
