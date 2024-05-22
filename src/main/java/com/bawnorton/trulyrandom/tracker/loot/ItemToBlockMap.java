package com.bawnorton.trulyrandom.tracker.loot;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemToBlockMap extends HashMap<Item, List<Block>> {
    public ItemToBlockMap() {
        super();
    }

    public ItemToBlockMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ItemToBlockMap(Map<Item, List<Block>> map) {
        super(map);
    }
}
