package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.screen.BlockStateGuiRenderer;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.function.Consumer;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z"
            )
    )
    private boolean setRenderedEntity(BlockEntityRenderDispatcher instance, BlockEntity entity, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay, Operation<Boolean> original) {
        Consumer<BlockEntity> transformer = BlockStateGuiRenderer.BLOCK_ENTITY_TRANSFORMER.get();
        Consumer<BlockEntity> detransformer = BlockStateGuiRenderer.BLOCK_ENTITY_DETRANSFORMER.get();
        if(transformer != null) {
            transformer.accept(entity);
        }
        boolean result = original.call(instance, entity, matrix, vertexConsumerProvider, light, overlay);
        if(detransformer != null) {
            detransformer.accept(entity);
        }
        return result;
    }
}
