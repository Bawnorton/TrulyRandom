package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.LivingEntityGuiRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.BuiltInRegistries;
import net.minecraft.text.Text;

public class EntityGraphElement extends GraphElement implements LivingEntityGuiRenderer {
    private final EntityType<?> entityType;

    public EntityGraphElement(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        Entity entity = entityType.create(client.world, SpawnReason.COMMAND);
        if(!(entity instanceof LivingEntity livingEntity)) return;

        render(context, mouseX, mouseY, livingEntity, x, y, scale);
    }

    @Override
    protected Text getTooltip() {
        return entityType.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityGraphElement element) {
            return entityType.equals(element.entityType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return entityType.hashCode();
    }

    @Override
    public String toString() {
        return "EntityGraphElement[%s]".formatted(BuiltInRegistries.ENTITY_TYPE.getId(entityType));
    }
}
