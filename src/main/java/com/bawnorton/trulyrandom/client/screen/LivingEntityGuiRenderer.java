package com.bawnorton.trulyrandom.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public interface LivingEntityGuiRenderer {
    default void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, LivingEntity entity, float x, float y, float scale) {
        AABB box = entity.getBoundingBox();
        float renderScale = 1.0f / (float) (Math.max(box.getXsize(), Math.max(box.getYsize(), box.getZsize())));
        int x0 = (int) ((x - 16) * scale);
        int y0 = (int) ((y - 16) * scale);
        int x1 = (int) ((x + 16) * scale);
        int y1 = (int) ((y + 16) * scale);
        int size = (int) (10 * renderScale * scale);
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                graphics,
                x0,
                y0,
                x1,
                y1,
                size,
                0,
                mouseX,
                mouseY,
                entity
        );
    }
}
