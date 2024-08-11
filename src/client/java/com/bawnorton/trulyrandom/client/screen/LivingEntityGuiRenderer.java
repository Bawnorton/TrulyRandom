package com.bawnorton.trulyrandom.client.screen;

import com.bawnorton.trulyrandom.client.mixin.accessor.GuardianEntityAccessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface LivingEntityGuiRenderer {
    default void render(LivingEntity livingEntity, MinecraftClient client, float x, float y, float scale) {
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.translate(x * scale, y * scale, 1050);
        modelViewStack.scale(scale, scale, -1);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrices = new MatrixStack();

        Box box = livingEntity.getBoundingBox();
        float entityScale = 1.0f / (float) (Math.max(box.getLengthX(), Math.max(box.getLengthY(), box.getLengthZ())));
        matrices.scale(entityScale, entityScale, entityScale);
        matrices.translate(0, 5, 0);

        switch (livingEntity) {
            case SquidEntity ignored -> {
                matrices.translate(1, -6, 0);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-75));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-22.5F));
                matrices.scale(0.8f, 0.8f, 0.8f);
            }
            case SlimeEntity ignored -> matrices.translate(0, -3, 0);
            case ElderGuardianEntity ignored -> matrices.translate(3, 6, 0);
            case GuardianEntity guardianEntity -> ((GuardianEntityAccessor) guardianEntity).setTailAngle(0.5f);
            case EnderDragonEntity ignored -> {
                matrices.scale(-4, 4, 4);
                matrices.translate(-20, 0, 0);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45));
            }
            case BatEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 2, 0);
            }
            case FoxEntity ignored -> matrices.translate(2, 2, 0);
            case AbstractHorseEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 6, 0);
            }
            case PufferfishEntity pufferfishEntity -> {
                pufferfishEntity.setPuffState(PufferfishEntity.FULLY_PUFFED);
                matrices.translate(0, -1, 0);
            }
            case FishEntity ignored -> matrices.translate(0, -4, 0);
            case SilverfishEntity ignored -> matrices.translate(1, -3, 0);
            case VillagerEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 6, 0);
            }
            case WitchEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 8, 0);
            }
            case RabbitEntity ignored -> {
                matrices.scale(1.5f, 1.5f, 1.5f);
                matrices.translate(0.5f, -1, 0);
            }
            case WitherEntity ignored -> {
                matrices.scale(3, 3, 3);
                matrices.translate(-1, 18, 0);
            }
            case EndermiteEntity ignored -> matrices.translate(0, -3, 0);
            case AbstractSkeletonEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 6, 0);
            }
            case ZombieEntity zombieEntity -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 6, 0);
                if(zombieEntity instanceof ZombieVillagerEntity zombieVillager) {
                    zombieVillager.setVillagerData(new VillagerData(VillagerType.PLAINS, VillagerProfession.CLERIC, 0));
                }
            }
            case EndermanEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 8, 0);
            }
            case FrogEntity ignored -> matrices.translate(0, -3, 0);
            case IllusionerEntity ignored -> {
                matrices.scale(2, 2, 2);
                matrices.translate(0, 6, 0);
            }
            default -> {}
        }

        matrices.scale(10, 10, 10);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(MathHelper.PI);
        Quaternionf quaternionf2 = (new Quaternionf()).rotateX(-30 * MathHelper.PI / 180);
        quaternionf.mul(quaternionf2);
        matrices.multiply(quaternionf);

        float bodyYaw = livingEntity.getBodyYaw();
        float yaw = livingEntity.getYaw();
        float pitch = livingEntity.getPitch();
        float headYaw = livingEntity.getHeadYaw();
        float prevHeadYaw = livingEntity.prevHeadYaw;

        livingEntity.setBodyYaw(135F);
        livingEntity.setYaw(135F);
        livingEntity.setPitch(0F);
        livingEntity.setHeadYaw(livingEntity.getYaw());
        livingEntity.prevHeadYaw = livingEntity.getYaw();

        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.setShaderLights(new Vector3f(-0.2F, 1.0F, -1.0F), new Vector3f(0.2F, -1.0F, 0.0F));
        quaternionf2.conjugate();

        EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderDispatcher();
        entityRenderDispatcher.setRotation(quaternionf2);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.render(livingEntity, 0.0, 0.0, 0.0, 0.0F, 1.0F, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE);
        entityRenderDispatcher.setRenderShadows(true);

        immediate.draw();

        livingEntity.bodyYaw = bodyYaw;
        livingEntity.setYaw(yaw);
        livingEntity.setPitch(pitch);
        livingEntity.prevHeadYaw = headYaw;
        livingEntity.headYaw = prevHeadYaw;

        matrices.pop();
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
