package com.bawnorton.trulyrandom.client.random.model;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.random.ClientRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.ItemModelModuleState;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class ItemModelRandomiser extends ModelRandomiser<Identifier> {
    @Override
    public void randomise(Minecraft minecraft, long seed) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
        modelShuffler.trulyrandom$shuffleModels(seed);
        syncWithBlockModels(minecraft, false);
        ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getRedirectMap());
    }

    public boolean syncWithBlockModels(Minecraft minecraft, boolean blockModelsReset) {
        ClientRandomiser randomiser = TrulyRandomClient.getRandomiser();
        Modules modules = randomiser.getModules();
        ItemModelModuleState state = modules.getState(Module.ITEM_MODELS, ItemModelModuleState.class);
        if (state.isEnabled() && state.isMatchingBlockModelRandomisation()) {
            if (modules.isEnabled(Module.BLOCK_MODELS)) {
                BlockModelRandomiser blockModelRandomiser = randomiser.getBlockModelRandomiser();
                UnaryMap<BlockState> blockStateRedirectMap = blockModelRandomiser.getRedirectMap(minecraft);
                UnaryMap<Identifier> itemRedirectMap = new UnaryHashMap<>(this.getRedirectMap(minecraft));

                UnaryMap<Identifier> lookup = new UnaryHashMap<>();
                for (Map.Entry<Identifier, Identifier> entry : itemRedirectMap.entrySet()) {
                    lookup.put(entry.getValue(), entry.getKey());
                }

                for (Map.Entry<BlockState, BlockState> entry : blockStateRedirectMap.entrySet()) {
                    BlockState from = entry.getKey();
                    BlockState to = entry.getValue();
                    Item fromItem = from.getBlock().asItem();
                    Item toItem = to.getBlock().asItem();
                    if(fromItem == Items.AIR || toItem == Items.AIR) continue;

                    Identifier fromId = BuiltInRegistries.ITEM.getKey(fromItem);
                    Identifier toId = BuiltInRegistries.ITEM.getKey(toItem);
                    if (toId.equals(itemRedirectMap.get(fromId))) continue;

                    Identifier oldToId = itemRedirectMap.get(fromId);
                    Identifier otherKey = lookup.get(toId);

                    if (otherKey != null && !otherKey.equals(fromId)) {
                        itemRedirectMap.put(otherKey, oldToId);
                        if (oldToId != null) {
                            lookup.put(oldToId, otherKey);
                        }
                    }

                    itemRedirectMap.put(fromId, toId);
                    lookup.put(toId, fromId);
                }
                updateItemModels(itemRedirectMap);
            } else if (blockModelsReset) {
                ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
                modelShuffler.trulyrandom$shuffleModels(state.getSeed());
                ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(modelShuffler.trulyrandom$getRedirectMap());
            }
            return true;
        }
        return false;
    }

    @Override
    public void reset(Minecraft minecraft) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
        modelShuffler.trulyrandom$resetModels();
    }

    public void reloadModels(Minecraft minecraft) {
    }

    @Override
    public UnaryMap<Identifier> getRedirectMap(Minecraft minecraft) {
        ModelShuffler.Items modelShuffler = (ModelShuffler.Items) minecraft.getModelManager();
        return modelShuffler.trulyrandom$getRedirectMap();
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
