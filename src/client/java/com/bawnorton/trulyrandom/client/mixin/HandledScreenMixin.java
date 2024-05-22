package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @WrapOperation(method = "drawSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void drawBrokenHint(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, Operation<Void> original) {
        if (!TrulyRandomClient.getRandomiser().getLootTableTracker().knowsItemLootTable(stack.getItem())) {
            original.call(instance, textRenderer, stack, x, y, countOverride);
            return;
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 0.0F, 1.0F);
        original.call(instance, textRenderer, stack, x, y, countOverride);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
