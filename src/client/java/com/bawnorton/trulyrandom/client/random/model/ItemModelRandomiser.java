package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.RandomiserModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;

public class ItemModelRandomiser extends RandomiserModule implements ModelRandomiser {
    @Override
    public ModelShuffler<?> getModelShuffler(MinecraftClient client) {
        return (ModelShuffler.Items) client.getItemRenderer().getModels();
    }

    public void reloadModels(MinecraftClient client) {
        ItemModels models = client.getItemRenderer().getModels();
        if (models != null && client.isFinishedLoading()) models.reloadModels();
    }
}
