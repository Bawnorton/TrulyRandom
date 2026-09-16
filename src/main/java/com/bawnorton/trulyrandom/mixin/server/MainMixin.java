package com.bawnorton.trulyrandom.mixin.server;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.server.ServerSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.DataFixer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import net.minecraft.server.Main;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@MixinEnvironment(type = MixinEnvironment.Env.SERVER)
@Mixin(Main.class)
abstract class MainMixin {
    @Unique
    private static OptionSpec<Path> trulyrandom$settingsPath;

    @Inject(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Ljoptsimple/OptionParser;nonOptions()Ljoptsimple/NonOptionArgumentSpec;"
            )
    )
    private static void parseTrulyRandomArg(String[] args, CallbackInfo ci, @Local OptionParser parser) {
        trulyrandom$settingsPath = parser.accepts("trulyrandom").withRequiredArg().withValuesConvertedBy(new PathConverter());
    }

    @SuppressWarnings("ConstantValue")
    @Definition(id = "WorldStem", type = WorldStem.class)
    @Expression("? = @((WorldStem) ?)")
    @ModifyExpressionValue(
            method = "main",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private static WorldStem initSettings(WorldStem original, @Local WorldLoader.InitConfig initConfig, @Local OptionSet options) {
        if(initConfig.packConfig().initMode()) {
            if((Object) original instanceof ModulesHolder modulesHolder) {
                Path path = Optional.ofNullable(options.valueOf(trulyrandom$settingsPath)).orElse(Paths.get("trulyrandom.json"));
                Modules modules = ServerSettings.loadSettings(path, new Modules());
                modulesHolder.trulyrandom$setRandomiserModules(modules);
            }
        }
        return original;
    }

    @SuppressWarnings("ConstantValue")
    @WrapOperation(
            method = "lambda$main$3",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/Thread;Lnet/minecraft/world/level/storage/LevelStorageSource$LevelStorageAccess;Lnet/minecraft/server/packs/repository/PackRepository;Lnet/minecraft/server/WorldStem;Ljava/util/Optional;Lnet/minecraft/server/dedicated/DedicatedServerSettings;Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/server/Services;)Lnet/minecraft/server/dedicated/DedicatedServer;"
            )
    )
    private static DedicatedServer attachModules(Thread serverThread, LevelStorageSource.LevelStorageAccess levelStorageSource, PackRepository packRepository, WorldStem worldStem, Optional gameRules, DedicatedServerSettings settings, DataFixer fixerUpper, Services services, Operation<DedicatedServer> original) {
        if ((Object) worldStem instanceof ModulesHolder modulesHolder) {
            TrulyRandom.setWorldGenModules(modulesHolder.trulyrandom$getRandomiserModules());
        }
        return original.call(serverThread, levelStorageSource, packRepository, worldStem, gameRules, settings, fixerUpper, services);
    }
}
