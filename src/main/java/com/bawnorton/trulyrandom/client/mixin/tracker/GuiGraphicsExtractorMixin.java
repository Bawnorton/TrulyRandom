package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.TrackerHintExtracter;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@MixinEnvironment("client")
@Mixin(GuiGraphicsExtractor.class)
abstract class GuiGraphicsExtractorMixin implements TrackerHintExtracter {
    @WrapMethod(
            method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
    )
    private void extractStackHints(Font font, ItemStack itemStack, int x, int y, String countText, Operation<Void> original) {
        original.call(font, itemStack, x, y, countText);
        if(TrulyRandomClient.getRandomiser().getLootTableTracker().shoulDisplayTracker()) {
            extractStackHints((GuiGraphicsExtractor) (Object) this, x, y, itemStack.getItem());
        }
    }
}
