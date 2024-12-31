package com.bawnorton.trulyrandom.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;

public interface LivingEntityGuiRenderer {
    default void render(DrawContext context, int mouseX, int mouseY, LivingEntity livingEntity, float x, float y, float scale) {
        Box box = livingEntity.getBoundingBox();
        float renderScale = 1.0f / (float) (Math.max(box.getLengthX(), Math.max(box.getLengthY(), box.getLengthZ())));
        int x1 = (int) (x - 16);
        int y1 = (int) (y - 16);
        int x2 = (int) (x + 16);
        int y2 = (int) (y + 16);
        InventoryScreen.drawEntity(context, x1, y1, x2, y2, (int) (15 * renderScale), 0.0625f, mouseX / scale, mouseY / scale, livingEntity);
    }
}
