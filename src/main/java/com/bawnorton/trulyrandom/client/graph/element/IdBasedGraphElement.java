package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.network.chat.Component;

public abstract class IdBasedGraphElement extends GraphElement {
    protected final LootTableIdentifier lootTableId;

    protected IdBasedGraphElement(LootTableIdentifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    protected Component getTooltip() {
        return Component.literal(lootTableId.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IdBasedGraphElement idBasedGraphElement) {
            return lootTableId.equals(idBasedGraphElement.lootTableId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return lootTableId.hashCode();
    }

    @Override
    public String toString() {
        return "%s[%s]".formatted(getClass().getSimpleName(), lootTableId);
    }
}
