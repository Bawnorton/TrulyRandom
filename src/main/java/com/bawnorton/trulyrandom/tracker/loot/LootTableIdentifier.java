package com.bawnorton.trulyrandom.tracker.loot;

import net.minecraft.util.Identifier;

public class LootTableIdentifier {
    private final String namespace;
    private final String[] segments;

    public LootTableIdentifier(Identifier lootTableId) {
        this.namespace = lootTableId.getNamespace();
        this.segments = lootTableId.getPath().split("/");
    }

    public static LootTableIdentifier from(Identifier lootTableId) {
        return new LootTableIdentifier(lootTableId);
    }

    public boolean isFromBlock() {
        if(segments.length == 0) return false;

        return segments[0].equals("blocks");
    }

    public Identifier getSourceId() {
        if(isFromBlock()) {
            return Identifier.of(namespace, segments[1]);
        }
        return null;
    }
}
