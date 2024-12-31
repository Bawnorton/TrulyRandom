package com.bawnorton.trulyrandom.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Unique
    private static final ThreadLocal<Modules> trulyrandom$MODULES = new ThreadLocal<>();

    @Inject(
            method = "loadDataPacks(Lnet/minecraft/resource/ResourcePackManager;Lnet/minecraft/resource/DataConfiguration;ZZ)Lnet/minecraft/resource/DataConfiguration;",
            at = @At("HEAD")
    )
    private static void captureModules(ResourcePackManager resourcePackManager, DataConfiguration dataConfiguration, boolean initMode, boolean safeMode, CallbackInfoReturnable<DataConfiguration> cir) {
        trulyrandom$MODULES.set(((ModulesHolder) (Object) dataConfiguration).trulyrandom$getRandomiserModules());
    }

    @ModifyReturnValue(
            method = "loadDataPacks(Lnet/minecraft/resource/ResourcePackManager;Ljava/util/Collection;Lnet/minecraft/resource/featuretoggle/FeatureSet;Z)Lnet/minecraft/resource/DataConfiguration;",
            at = @At("TAIL")
    )
    private static DataConfiguration attachModules(DataConfiguration dataConfiguration) {
        ((ModulesHolder) (Object) dataConfiguration).trulyrandom$setRandomiserModules(trulyrandom$MODULES.get());
        trulyrandom$MODULES.remove();
        return dataConfiguration;
    }
}
