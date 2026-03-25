package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldListWidget.WorldEntry.class)
public abstract class WorldListWidget$WorldEntryMixin {
    @Shadow
    @Final
    LevelSummary level;

    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "play", at = @At("HEAD"))
    private void loadRandomiserModulesFromWorld(CallbackInfo ci) {
        ModulesHolder dataConfiguration = (ModulesHolder) (Object) level.getLevelInfo().getDataConfiguration();
        Modules modules = dataConfiguration.trulyrandom$getRandomiserModules();
        ((ModulesHolder) client).trulyrandom$setRandomiserModules(modules);
    }
}
