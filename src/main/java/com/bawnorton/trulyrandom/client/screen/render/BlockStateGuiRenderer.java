package com.bawnorton.trulyrandom.client.screen.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class BlockStateGuiRenderer extends SpecialGuiElementRenderer<BlockStateElementRenderState> {
    public BlockStateGuiRenderer(VertexConsumerProvider.Immediate immediate) {
        super(immediate);
    }

    @Override
    public Class<BlockStateElementRenderState> getElementClass() {
        return BlockStateElementRenderState.class;
    }

    @Override
    protected void render(BlockStateElementRenderState state, MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_FLAT);

        matrices.translate(0, -16, 50);
        matrices.scale(15, -15, -50);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.angle()));
        matrices.translate(-0.5, -0.5, -0.5);

        client.getBlockRenderManager().renderBlockAsEntity(state.blockState(), matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected String getName() {
        return "trulyrandom:blockstate";
    }
}
