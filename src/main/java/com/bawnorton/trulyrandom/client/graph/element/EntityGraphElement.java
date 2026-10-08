package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.render.LivingEntityGuiRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class EntityGraphElement extends GraphElement implements LivingEntityGuiRenderer {
    private final EntityType<?> entityType;

    public EntityGraphElement(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        Entity entity = entityType.create(minecraft.level, EntitySpawnReason.COMMAND);
        if(!(entity instanceof LivingEntity livingEntity)) return;

        extractRenderState(graphics, mouseX, mouseY, livingEntity, x, y, scale);
    }

    @Override
    public int getColour() {
        return CommonColors.SOFT_RED;
    }

    @Override
    protected Component getTooltip() {
        return entityType.getDescription();
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
        return "EntityGraphElement[%s]".formatted(BuiltInRegistries.ENTITY_TYPE.getKey(entityType));
    }
}
