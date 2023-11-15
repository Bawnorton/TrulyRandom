package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.ModelShuffler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow public abstract BakedModelManager getBakedModelManager();
    @Shadow public abstract ItemRenderer getItemRenderer();

    @Shadow @Final public WorldRenderer worldRenderer;

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(method = "onFinishedLoading", at = @At("TAIL"))
    private void reloadModels(CallbackInfo ci) {
        ModelShuffler.BlockStates blockStates = (ModelShuffler.BlockStates) getBakedModelManager().getBlockModels();
        ModelShuffler.Items items = (ModelShuffler.Items) getItemRenderer().getModels();
        if(blockStates.trulyRandom$isShuffled()) {
            blockStates.trulyrandom$shuffleModels(new Random(TrulyRandomClient.getRandomiser().getLocalSeed()));
        } else {
            blockStates.trulyrandom$resetModels();
        }
        if(items.trulyRandom$isShuffled()) {
            items.trulyrandom$shuffleModels(new Random(TrulyRandomClient.getRandomiser().getLocalSeed()));
        } else {
            items.trulyrandom$resetModels();
        }
        worldRenderer.reload();
        itemRenderer.getModels().reloadModels();
    }
}
