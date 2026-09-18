package com.bawnorton.trulyrandom.mixin.server;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletionStage;

@MixinEnvironment(type = MixinEnvironment.Env.SERVER)
@Mixin(WorldLoader.class)
abstract class WorldLoaderMixin {
    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "lambda$load$2",
            at = @At("RETURN")
    )
    private static CompletionStage<WorldStem> captureAndAttachModules(CompletionStage<WorldStem> original, @Local WorldDataConfiguration worldDataConfiguration) {
        if ((Object) worldDataConfiguration instanceof ModulesHolder originalHolder) {
            return original.thenApply(resources -> {
                if ((Object) resources instanceof ModulesHolder holder) {
                    holder.trulyrandom$setRandomiserModules(originalHolder.trulyrandom$getRandomiserModules());
                }
                return resources;
            });
        }
        return original;
    }
}
