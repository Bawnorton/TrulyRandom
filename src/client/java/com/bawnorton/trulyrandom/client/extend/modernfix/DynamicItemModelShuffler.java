package com.bawnorton.trulyrandom.client.extend.modernfix;

import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public interface DynamicItemModelShuffler extends ModelShuffler.Items {
    default void trulyrandom$shuffleModels(long seed) {
        List<Item> items = Registries.ITEM.stream()
                                          .sorted(Comparator.comparingInt(Registries.ITEM::getRawId))
                                          .collect(Collectors.toList());
        Collections.shuffle(items, new Random(seed));
        trulyrandom$resetModels();
        for (int i = 0; i < items.size(); i++) {
            Item originalItem = items.get(i);
            Item randomItem = items.get((i + 1) % items.size());
            trulyrandom$getOriginalRandomisedMap().put(originalItem, randomItem);
        }
    }
}
