package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements MinecraftClientExtender {
    @Shadow
    @Final
    public WorldRenderer worldRenderer;
    @Shadow
    @Final
    private ItemRenderer itemRenderer;

    @Unique
    private boolean trulyrandom$finishedLoading = false;

    @Unique
    private boolean trulyrandom$isResizing = false;

    @Shadow
    public abstract BakedModelManager getBakedModelManager();

    @Shadow
    public abstract ItemRenderer getItemRenderer();

    @Inject(method = "collectLoadTimes", at = @At("HEAD"))
    private void reloadModelsAfterResourceReload(CallbackInfo ci) {
        trulyrandom$finishedLoading = true;
        ModelShuffler.BlockStates blockStates = (ModelShuffler.BlockStates) getBakedModelManager().getBlockModels();
        ModelShuffler.Items items = (ModelShuffler.Items) getItemRenderer().getModels();
        if (blockStates.trulyrandom$isShuffled()) {
            blockStates.trulyrandom$shuffleModels(TrulyRandomClient.getRandomiser().getModules().getSeed(Module.BLOCK_MODELS));
            ClientRandomiseEvents.BLOCK_MODELS.invoker().onBlockModels(blockStates.trulyrandom$getRedirectMap());
        } else {
            blockStates.trulyrandom$resetModels();
        }
        if (items.trulyrandom$isShuffled()) {
            items.trulyrandom$shuffleModels(TrulyRandomClient.getRandomiser().getModules().getSeed(Module.ITEM_MODELS));
            ClientRandomiseEvents.ITEM_MODELS.invoker().onItemModels(items.trulyrandom$getRedirectMap());
        } else {
            items.trulyrandom$resetModels();
        }
        worldRenderer.reload();
        itemRenderer.getModels().reloadModels();
    }

    @WrapOperation(
            method = "onResolutionChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;resize(Lnet/minecraft/client/MinecraftClient;II)V"
            )
    )
    private void trackResize(Screen instance, MinecraftClient client, int width, int height, Operation<Void> original) {
        trulyrandom$isResizing = true;
        original.call(instance, client, width, height);
        trulyrandom$isResizing = false;
    }

    @Override
    public boolean trulyrandom$isFinishedLoading() {
        return trulyrandom$finishedLoading;
    }

    @Override
    public boolean trulyrandom$isResizing() {
        return trulyrandom$isResizing;
    }
}
