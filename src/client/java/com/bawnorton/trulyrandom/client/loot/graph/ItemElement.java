package com.bawnorton.trulyrandom.client.loot.graph;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;

public class ItemElement extends GraphElement {
    private final Item item;

    public ItemElement(Item item) {
        this.item = item;
    }

    @Override
    public void render(DrawContext context, int x, int y) {
        context.drawItemWithoutEntity(item.getDefaultStack(), x - 8, y - 8);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemElement itemElement) {
            return itemElement.item.equals(item);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ItemElement[%s]".formatted(item.toString());
    }
}
