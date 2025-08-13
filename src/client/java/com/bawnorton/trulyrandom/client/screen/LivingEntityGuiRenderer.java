package com.bawnorton.trulyrandom.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static net.minecraft.client.gui.screen.ingame.InventoryScreen.drawEntity;

public interface LivingEntityGuiRenderer {
    default void render(DrawContext context, int mouseX, int mouseY, LivingEntity entity, float x, float y, float scale) {
        Box box = entity.getBoundingBox();
        float renderScale = 1.0f / (float) (Math.max(box.getLengthX(), Math.max(box.getLengthY(), box.getLengthZ())));
        int x1 = (int) ((x - 16) * scale);
        int y1 = (int) ((y - 16) * scale);
        int x2 = (int) ((x + 16) * scale);
        int y2 = (int) ((y + 16) * scale);
        int size = (int) (10 * renderScale);
        float f = (x1 + x2) / 2.0F;
        float g = (y1 + y2) / 2.0F;
        float h = (float)Math.atan((f - mouseX) / 40.0F);
        float i = (float)Math.atan((g - mouseY) / 40.0F);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0F * (float) (Math.PI / 180.0));
        quaternionf.mul(quaternionf2);
        float j = entity.bodyYaw;
        float k = entity.getYaw();
        float l = entity.getPitch();
        float m = entity.lastHeadYaw;
        float n = entity.headYaw;
        entity.bodyYaw = 180.0F + h * 20.0F;
        entity.setYaw(180.0F + h * 40.0F);
        entity.setPitch(-i * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.lastHeadYaw = entity.getYaw();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + 0.0625F * renderScale, 0.0F);
        float p = size / renderScale * scale;
        drawEntity(context, x1, y1, x2, y2, p, vector3f, quaternionf, quaternionf2, entity);
        entity.bodyYaw = j;
        entity.setYaw(k);
        entity.setPitch(l);
        entity.lastHeadYaw = m;
        entity.headYaw = n;
    }
}
