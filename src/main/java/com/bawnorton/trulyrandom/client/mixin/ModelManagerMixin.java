package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.*;

@MixinEnvironment("client")
@Mixin(ModelManager.class)
abstract class ModelManagerMixin implements ModelShuffler.Items {
    @Unique
    private final UnaryMap<Identifier> trulyrandom$redirectMap = new UnaryHashMap<>();
    @Shadow
    private Map<Identifier, ItemModel> bakedItemStackModels;

    @ModifyVariable(method = "getItemModel", at = @At("HEAD"), argsOnly = true)
    private Identifier getShuffledModel(Identifier id) {
        return trulyrandom$redirectMap.getOrDefault(id, id);
    }

    @Override
    public void trulyrandom$shuffleModels(long seed) {
        if (bakedItemStackModels == null) return;

        List<Identifier> ids = new ArrayList<>(bakedItemStackModels.keySet());

        trulyrandom$resetModels();
        Collections.shuffle(ids, new Random(seed));
        for (int i = 0; i < ids.size(); i++) {
            Identifier original = ids.get(i);
            Identifier random = ids.get((i + 1) % ids.size());

            trulyrandom$redirectMap.put(original, random);
        }
    }

    @Override
    public UnaryMap<Identifier> trulyrandom$getRedirectMap() {
        return trulyrandom$redirectMap;
    }

    @Override
    public void trulyrandom$resetModels() {
        trulyrandom$redirectMap.clear();
    }

    @Override
    public boolean trulyrandom$isShuffled() {
        return !trulyrandom$redirectMap.isEmpty();
    }
}
