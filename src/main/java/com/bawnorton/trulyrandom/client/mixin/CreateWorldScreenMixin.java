package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.client.screen.TrulyRandomSettingsScreen;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen implements ModulesHolder {
    @Shadow @Final
    WorldCreator worldCreator;
    @Unique
    private Modules trulyrandom$modules = new Modules();

    protected CreateWorldScreenMixin(Text title) {
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
            method = "createLevelInfo",
            at = @At("HEAD")
    )
    private void attachModules(boolean debugWorld, CallbackInfoReturnable<LevelInfo> cir) {
        ((ModulesHolder) (Object) worldCreator.getGeneratorOptionsHolder().dataConfiguration()).trulyrandom$setRandomiserModules(trulyrandom$modules);
        ((ModulesHolder) client).trulyrandom$setRandomiserModules(trulyrandom$modules);
    }

    @Mixin(targets = "net.minecraft.client.gui.screen.world.CreateWorldScreen$MoreTab")
    public abstract static class MoreTabMixin extends GridScreenTab {
        @Shadow @Final
        CreateWorldScreen field_42178;

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
            Modules modules = ((ModulesHolder) field_42178).trulyrandom$getRandomiserModules();
            client.setScreen(new TrulyRandomSettingsScreen(client.currentScreen, modules, newModules -> ((ModulesHolder) field_42178).trulyrandom$setRandomiserModules(newModules)));
        }
    }
}
