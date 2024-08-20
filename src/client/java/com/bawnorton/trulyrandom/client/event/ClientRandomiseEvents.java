package com.bawnorton.trulyrandom.client.event;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.util.collection.UnaryHashMap;
import com.bawnorton.trulyrandom.util.collection.UnaryMap;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;

public class ClientRandomiseEvents {
    private ClientRandomiseEvents() {
    }

    public static final Event<BlockModels> BLOCK_MODELS = EventFactory.createArrayBacked(BlockModels.class, callbacks -> randomisedMap -> {
        for (BlockModels callback : callbacks) {
            callback.onBlockModels(randomisedMap);
        }
        TrulyRandomClient.getRandomiser().updateBlockModels(new UnaryHashMap<>(randomisedMap));
    });

    public static final Event<ItemModels> ITEM_MODELS = EventFactory.createArrayBacked(ItemModels.class, callbacks -> randomisedMap -> {
        for (ItemModels callback : callbacks) {
            callback.onItemModels(randomisedMap);
        }
        TrulyRandomClient.getRandomiser().updateItemModels(new UnaryHashMap<>(randomisedMap));
    });

    @FunctionalInterface
    public interface BlockModels {
        void onBlockModels(UnaryMap<BlockState> randomisedMap);
    }

    @FunctionalInterface
    public interface ItemModels {
        void onItemModels(UnaryMap<Item> randomisedMap);
    }
}
