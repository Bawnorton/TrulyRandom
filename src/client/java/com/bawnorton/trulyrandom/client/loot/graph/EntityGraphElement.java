package com.bawnorton.trulyrandom.client.loot.graph;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;

public class EntityGraphElement extends GraphElement {
    private final EntityType<?> entityType;

    public EntityGraphElement(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public void render(DrawContext context, int x, int y) {

    }
}
