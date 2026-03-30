package com.bawnorton.trulyrandom.client.mixin.tracker;

import com.bawnorton.trulyrandom.client.TrulyRandomClient;
import com.bawnorton.trulyrandom.client.extend.TrackerHintExtracter;
import com.bawnorton.trulyrandom.random.module.Module;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinEnvironment("client")
@Mixin(AbstractContainerScreen.class)
abstract class AbstractContainerScreenMixin extends Screen implements TrackerHintExtracter {
    @Shadow protected int leftPos;
    @Final
    @Shadow protected int imageHeight;
    @Final
    @Shadow protected int imageWidth;

    @Shadow protected int topPos;

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "extractSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
            )
    )
    private void extractSlotHints(GuiGraphicsExtractor instance, Font font, ItemStack itemStack, int x, int y, String countText, Operation<Void> original) {
        original.call(instance, font, itemStack, x, y, countText);
        if(TrulyRandomClient.getRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) {
            extractStackHints(instance, x, y, itemStack.getItem());
        }
    }

    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(
            method = "mouseDragged",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void mouseDragInInvScreen(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
    }

    @SuppressWarnings("CancellableInjectionUsage")
    @Inject(
            method = "mouseScrolled",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void mouseScrolledInInvScreen(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
    }
}
