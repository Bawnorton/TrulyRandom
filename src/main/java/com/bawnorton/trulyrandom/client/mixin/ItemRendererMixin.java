package com.bawnorton.trulyrandom.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment("client")
@Mixin(ItemInHandRenderer.class)
abstract class ItemRendererMixin {
    /*@Inject(
            method = "renderItem(Lnet/minecraft/item/ModelTransformationMode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/extractRenderState/MultiBufferSource;II[ILnet/minecraft/client/extractRenderState/model/BakedModel;Lnet/minecraft/client/extractRenderState/RenderLayer;Lnet/minecraft/client/extractRenderState/item/ItemRenderState$Glint;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/extractRenderState/model/BakedModel;getTransformation()Lnet/minecraft/client/extractRenderState/model/json/ModelTransformation;"
            )
    )
    private void replaceWithSeenItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> stackLocalRef) {
        ItemModels models = getModels();
        ItemStack stack = stackLocalRef.get();
        Item seenItem = ((ModelShuffler.Items) models).trulyrandom$getRedirectMap().get(stack.getItem());
        stackLocalRef.set(seenItem == null ? stack : seenItem.getDefaultInstance());
    }*/
}
