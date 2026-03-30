package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@MixinEnvironment("client")
@Mixin(CreateWorldScreen.class)
abstract class CreateWorldScreenMixin extends Screen implements ModulesHolder {
    @Shadow @Final
    private WorldCreationUiState uiState;
    @Unique
    private Modules trulyrandom$modules = new Modules();

    protected CreateWorldScreenMixin(Component title) {
        super(title);
    }

    @Override
    public Modules trulyrandom$getRandomiserModules() {
        return trulyrandom$modules;
    }

    @Override
    public void trulyrandom$setRandomiserModules(Modules modules) {
        this.trulyrandom$modules = modules;
    }

    @Inject(
            method = "createLevelSettings",
            at = @At("HEAD")
    )
    private void attachModules(boolean debugWorld, CallbackInfoReturnable<LevelSettings> cir) {
        ((ModulesHolder) (Object) uiState.getSettings().dataConfiguration()).trulyrandom$setRandomiserModules(trulyrandom$modules);
        ((ModulesHolder) minecraft).trulyrandom$setRandomiserModules(trulyrandom$modules);
    }

    @MixinEnvironment("client")
    @Mixin(targets = "net.minecraft.client.gui.screens.worldselection.CreateWorldScreen$MoreTab")
    abstract static class MoreTabMixin extends GridLayoutTab {
        @Shadow @Final
        CreateWorldScreen this$0;

        protected MoreTabMixin(Component title) {
            super(title);
        }

        @Inject(method = "<init>", at = @At("TAIL"))
        private void addTrulyRandomSettingsButton(CallbackInfo ci, @Local(name = "helper") GridLayout.RowHelper rowHelper) {
            rowHelper.addChild(Button.builder(
                            Component.translatable("selectWorld.trulyrandom"),
                            _ -> openTrulyRandomSettings())
                    .width(210)
                    .build()
            );
        }

        @Unique
        private void openTrulyRandomSettings() {
            Minecraft minecraft = Minecraft.getInstance();
            Modules modules = ((ModulesHolder) this$0).trulyrandom$getRandomiserModules();
            minecraft.setScreen(new TrulyRandomSettingsScreen(minecraft.screen, modules, newModules -> ((ModulesHolder) this$0).trulyrandom$setRandomiserModules(newModules)));
        }
    }
}
