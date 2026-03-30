package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment("client")
@Mixin(WorldSelectionList.WorldListEntry.class)
abstract class WorldListWidget$WorldEntryMixin {
    @Shadow
    @Final
    private LevelSummary summary;

    @Shadow @Final private Minecraft minecraft;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "joinWorld", at = @At("HEAD"))
    private void loadRandomiserModulesFromWorld(CallbackInfo ci) {
        ModulesHolder dataConfiguration = (ModulesHolder) (Object) summary.getSettings().dataConfiguration();
        Modules modules = dataConfiguration.trulyrandom$getRandomiserModules();
        ((ModulesHolder) minecraft).trulyrandom$setRandomiserModules(modules);
    }
}
