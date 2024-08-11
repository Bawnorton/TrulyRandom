package com.bawnorton.trulyrandom.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import java.util.function.Consumer;

public interface BlockStateGuiRenderer {
    ThreadLocal<Consumer<BlockEntity>> BLOCK_ENTITY_TRANSFORMER = new ThreadLocal<>();
    ThreadLocal<Consumer<BlockEntity>> BLOCK_ENTITY_DETRANSFORMER = new ThreadLocal<>();

    default void render(DrawContext context, MinecraftClient client, int x, int y, float scale, BlockState state) {
        render(context, client, x, y, 45, scale, state, blockEntity -> {}, blockEntity -> {});
    }

    default void render(DrawContext context, MinecraftClient client, int x, int y, int angle, float scale, BlockState state) {
        render(context, client, x, y, angle, scale, state, blockEntity -> {}, blockEntity -> {});
    }


    default void render(DrawContext context, MinecraftClient client, int x, int y, int angle, float scale, BlockState state, Consumer<BlockEntity> transformer, Consumer<BlockEntity> detransformer) {
        DiffuseLighting.disableGuiDepthLighting();

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x, y, 100);
        matrices.scale(15 * scale, -15 * scale, 40);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        matrices.translate(-0.5, -0.5, -0.5);

        VertexConsumerProvider.Immediate vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
        BLOCK_ENTITY_TRANSFORMER.set(transformer);
        BLOCK_ENTITY_DETRANSFORMER.set(detransformer);
        client.getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        BLOCK_ENTITY_TRANSFORMER.remove();
        BLOCK_ENTITY_DETRANSFORMER.remove();

        RenderSystem.setShaderLights(new Vector3f(1, -0.5f, 0), new Vector3f(0, -1, 0));
        vertexConsumers.draw();
        DiffuseLighting.enableGuiDepthLighting();

        matrices.pop();
    }
}
