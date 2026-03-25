package com.bawnorton.trulyrandom.mixin.saved_data;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
abstract class ServerLevelMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void initRandomiserSaveLoader(CallbackInfo ci) {
        RandomiserSaveLoader.TYPE = new SavedDataType<>(
                TrulyRandom.id("randomiser"),
                () -> new RandomiserSaveLoader(getServer()),
                RandomiserSaveLoader.codec(getServer()),
                null
        );
    }
}
