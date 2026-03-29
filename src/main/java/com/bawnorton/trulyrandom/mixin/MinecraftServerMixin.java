package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Unique
    private static final ThreadLocal<Modules> trulyrandom$MODULES = new ThreadLocal<>();

    @Inject(
            method = "configurePackRepository",
            at = @At("HEAD")
    )
    private static void captureModules(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean initMode, boolean safeMode, CallbackInfoReturnable<WorldDataConfiguration> cir) {
        trulyrandom$MODULES.set(((ModulesHolder) (Object) initialDataConfig).trulyrandom$getRandomiserModules());
    }

    @ModifyReturnValue(
            method = "configureRepositoryWithSelection",
            at = @At("TAIL")
    )
    private static WorldDataConfiguration attachModules(WorldDataConfiguration original) {
        ((ModulesHolder) (Object) original).trulyrandom$setRandomiserModules(trulyrandom$MODULES.get());
        trulyrandom$MODULES.remove();
        return original;
    }
}
