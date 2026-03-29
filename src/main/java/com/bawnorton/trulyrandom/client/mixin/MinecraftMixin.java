package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.event.ClientRandomiseEvents;
import com.bawnorton.trulyrandom.client.extend.MinecraftClientExtender;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
abstract class MinecraftMixin implements MinecraftClientExtender, ModulesHolder {
    @Shadow
    @Final
    public LevelRenderer levelRenderer;

    @Unique
    private boolean trulyrandom$finishedLoading = false;

    @Unique
    private boolean trulyrandom$isResizing = false;

    @Unique
    private Modules trulyrandom$modules;

    @Shadow
    public abstract ModelManager getModelManager();

    @Inject(method = "collectLoadTimes", at = @At("HEAD"))
    private void reloadModelsAfterResourceReload(CallbackInfo ci) {
        trulyrandom$finishedLoading = true;
        ModelShuffler.BlockStates blockStates = (ModelShuffler.BlockStates) getModelManager().getBlockModels();
        ModelShuffler.Items items = (ModelShuffler.Items) getModelManager();
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
        levelRenderer.reload();
    }

    @WrapOperation(
            method = "onResolutionChanged",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;resize(Lnet/minecraft/client/MinecraftClient;II)V"
            )
    )
    private void trackResize(Screen instance, Minecraft minecraft, int width, int height, Operation<Void> original) {
        trulyrandom$isResizing = true;
        original.call(instance, minecraft, width, height);
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

    @Override
    public void trulyrandom$setRandomiserModules(Modules modules) {
        trulyrandom$modules = modules;
    }

    @Override
    public Modules trulyrandom$getRandomiserModules() {
        return trulyrandom$modules;
    }

    @ModifyExpressionValue(
            method = "startIntegratedServer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;startServer(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"
            )
    )
    private <S extends MinecraftServer> S attachModules(S original) {
        TrulyRandom.setWorldGenModules(trulyrandom$modules);
        return original;
    }
}
