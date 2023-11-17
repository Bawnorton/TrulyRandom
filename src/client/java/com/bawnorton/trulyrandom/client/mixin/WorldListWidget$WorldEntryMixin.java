package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.DataConfigurationExtender;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
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
    @Shadow @Final private LevelSummary level;

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "start", at = @At("HEAD"))
    private void loadRandomiserModulesFromWorld(CallbackInfo ci) {
        DataConfigurationExtender dataConfiguration = (DataConfigurationExtender) (Object) level.getLevelInfo().getDataConfiguration();
        Randomiser randomiser = dataConfiguration.trulyRandom$getRandomiser();
        RandomiserSaveLoader.setDefaultRandomiser(randomiser.getSeed(), randomiser.getModules());
    }
}
