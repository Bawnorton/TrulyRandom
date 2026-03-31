package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.TrackerHintExtracter;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@MixinEnvironment("client")
@Mixin(Gui.class)
abstract class GuiMixin implements TrackerHintExtracter {
    @WrapOperation(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V"
            )
    )
    private void extractStackHints(GuiGraphicsExtractor instance, Font font, ItemStack itemStack, int x, int y, Operation<Void> original) {
        original.call(instance, font, itemStack, x, y);
        if(TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) {
            extractStackHints(instance, x, y, itemStack.getItem());
        }
    }
}
