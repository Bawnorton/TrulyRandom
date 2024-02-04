package com.bawnorton.trulyrandom.client.event;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import java.util.HashMap;
import java.util.Map;

public class ClientRandomiseEvents {
    private ClientRandomiseEvents() {
    }

    public static final Event<BlockModels> BLOCK_MODELS = EventFactory.createArrayBacked(BlockModels.class, callbacks -> randomisedMap -> {
        for (BlockModels callback : callbacks) {
            callback.onBlockModels(randomisedMap);
        }
        TrulyRandomClient.getRandomiser().updateBlockModels(new HashMap<>(randomisedMap));
    });

    public static final Event<ItemModels> ITEM_MODELS = EventFactory.createArrayBacked(ItemModels.class, callbacks -> randomisedMap -> {
        for (ItemModels callback : callbacks) {
            callback.onItemModels(randomisedMap);
        }
        TrulyRandomClient.getRandomiser().updateItemModels(new HashMap<>(randomisedMap));
    });

    @FunctionalInterface
    public interface BlockModels {
        void onBlockModels(Map<BlockState, BlockState> randomisedMap);
    }

    @FunctionalInterface
    public interface ItemModels {
        void onItemModels(Map<Item, Item> randomisedMap);
    }
}
