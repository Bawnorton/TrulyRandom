package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    protected CreateWorldScreenMixin(Text title) {
        super(title);
    }

    @Mixin(targets = "net.minecraft.client.gui.screen.world.CreateWorldScreen$MoreTab")
    public abstract static class MoreTabMixin extends GridScreenTab {
        protected MoreTabMixin(Text title) {
            super(title);
        }

        @Inject(method = "<init>", at = @At("TAIL"))
        private void addTrulyRandomSettingsButton(CallbackInfo ci, @Local GridWidget.Adder adder) {
            adder.add(ButtonWidget.builder(
                            Text.translatable("selectWorld.trulyrandom"),
                            button -> openTrulyRandomSettings())
                    .width(210)
                    .build()
            );
        }

        @Unique
        private void openTrulyRandomSettings() {
            MinecraftClient client = MinecraftClient.getInstance();
            client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, (modules, seed) -> RandomiserSaveLoader.setDefaultRandomiser(seed, modules)));
        }
    }
}
