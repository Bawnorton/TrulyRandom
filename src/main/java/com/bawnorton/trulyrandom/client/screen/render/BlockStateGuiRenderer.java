package com.bawnorton.trulyrandom.client.screen.render;


import com.bawnorton.trulyrandom.TrulyRandom;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;

//? if <=26.1.2 {
//import net.minecraft.client.renderer.MultiBufferSource;
//?} else {
import java.util.Optional;
import java.util.OptionalDouble;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.commands.RenderPass;
import net.minecraft.client.renderer.SubmitNodeCollector;
//?}

public class BlockStateGuiRenderer extends PictureInPictureRenderer<BlockStateElementRenderState> {
    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
    private final BlockModelRenderState blockModelRenderState;
    
    //? if <=26.1.2 {
    /*public BlockStateGuiRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        blockModelRenderState = new BlockModelRenderState();
    }

    @Override
    protected void renderToTexture(BlockStateElementRenderState renderState, PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);

        poseStack.translate(0, -16, 50);
        poseStack.scale(15, -15, -50);
        poseStack.mulPose(Axis.XP.rotationDegrees(30));
        poseStack.mulPose(Axis.YP.rotationDegrees(renderState.angle()));
        poseStack.translate(-0.5, -0.5, -0.5);

        BlockModel blockModel = minecraft.getModelManager().getBlockModelSet().get(renderState.blockState());
        blockModel.update(blockModelRenderState, renderState.blockState(), BLOCK_DISPLAY_CONTEXT, 42);
        FeatureRenderDispatcher featureRenderDispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        SubmitNodeStorage submitNodeStorage = featureRenderDispatcher.getSubmitNodeStorage();
        blockModelRenderState.submit(poseStack, submitNodeStorage, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
        featureRenderDispatcher.renderAllFeatures();
    }
    *///?} else {
    public BlockStateGuiRenderer() {
        blockModelRenderState = new BlockModelRenderState();
    }

    @Override
    protected void renderToTexture(BlockStateElementRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);

        poseStack.translate(0, -16, 50);
        poseStack.scale(15, -15, -50);
        poseStack.rotate(Axis.XP.rotationDegrees(30));
        poseStack.rotate(Axis.YP.rotationDegrees(renderState.angle()));
        poseStack.translate(-0.5, -0.5, -0.5);

        BlockModel blockModel = minecraft.getModelManager().getBlockModelSet().get(renderState.blockState());
        blockModel.update(blockModelRenderState, renderState.blockState(), BLOCK_DISPLAY_CONTEXT, 42);
        blockModelRenderState.submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
    }
    //?}

    @Override
    public Class<BlockStateElementRenderState> getRenderStateClass() {
        return BlockStateElementRenderState.class;
    }

    @Override
    protected String getTextureLabel() {
        return TrulyRandom.sid("blockstate");
    }
}
