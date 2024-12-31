package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.client.util.mixin.ModernFixConditionChecker;
import com.bawnorton.trulyrandom.client.util.mixin.annotation.AdvancedConditionalMixin;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(BakedModelManager.class)
@AdvancedConditionalMixin(checker = ModernFixConditionChecker.class, invert = true)
public abstract class VanillaItemsModelsMixin implements ModelShuffler.Items {
    @Unique
    private final UnaryMap<Identifier> trulyrandom$redirectMap = new UnaryHashMap<>();
    @Shadow
    private Map<Identifier, ItemModel> bakedItemModels;

    @ModifyVariable(method = "getItemModel", at = @At("HEAD"), argsOnly = true)
    private Identifier getShuffledModel(Identifier id) {
        return trulyrandom$redirectMap.getOrDefault(id, id);
    }

    @Override
    public void trulyrandom$shuffleModels(long seed) {
        if (bakedItemModels == null) return;

        List<Identifier> ids = new ArrayList<>(bakedItemModels.keySet());

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
