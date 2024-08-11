package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.LootTableIdentifier;
import net.minecraft.text.Text;

public abstract class IdBasedGraphElement extends GraphElement{
    protected final LootTableIdentifier lootTableId;

    protected IdBasedGraphElement(LootTableIdentifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    protected Text getTooltip() {
        return Text.of(lootTableId.toString());
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
