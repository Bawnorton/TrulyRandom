package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.notifications.NotificationManager;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.Proxy;
import java.util.Optional;

@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {
    @Unique
    private static final ThreadLocal<Modules> trulyrandom$MODULES = new ThreadLocal<>();

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void captureServer(CallbackInfo ci) {
        TrulyRandom.setServer((MinecraftServer) (Object) this);
    }

    @Inject(
            method = "stopServer",
            at = @At("HEAD")
    )
    private void clearServer(CallbackInfo ci) {
        TrulyRandom.setServer(null);
    }

    @SuppressWarnings("ConstantValue")
    @Inject(
            method = "configurePackRepository",
            at = @At("HEAD")
    )
    private static void captureModules(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean initMode, boolean safeMode, CallbackInfoReturnable<WorldDataConfiguration> cir) {
        if ((Object) initialDataConfig instanceof ModulesHolder modulesHolder) {
            trulyrandom$MODULES.set(modulesHolder.trulyrandom$getRandomiserModules());
        }
    }

    @SuppressWarnings("ConstantValue")
    @ModifyReturnValue(
            method = "configureRepositoryWithSelection",
            at = @At("TAIL")
    )
    private static WorldDataConfiguration attachModules(WorldDataConfiguration original) {
        if ((Object) original instanceof ModulesHolder modulesHolder) {
            modulesHolder.trulyrandom$setRandomiserModules(trulyrandom$MODULES.get());
            trulyrandom$MODULES.remove();
        }
        return original;
    }
}
