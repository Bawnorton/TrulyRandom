package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.TrackerHintDrawer;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements TrackerHintDrawer {
    @WrapOperation(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"
            )
    )
    private void drawHints(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, Operation<Void> original) {
        original.call(instance, textRenderer, stack, x, y, countOverride);
        if(TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
            drawHints(instance, x, y, stack.getItem());
        }
    }
}
