package com.bawnorton.trulyrandom.mixin.server;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.server.ServerSettings;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import net.minecraft.server.Main;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("ConstantValue")
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

    @ModifyArg(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;spin(Ljava/util/function/Function;)Lnet/minecraft/server/MinecraftServer;"
            )
    )
    private static <S> Function<Thread, S> attachModules(Function<Thread, S> factory, @Local WorldStem worldStem) {
        return thread -> {
            if ((Object) worldStem instanceof ModulesHolder modulesHolder) {
                TrulyRandom.setWorldGenModules(modulesHolder.trulyrandom$getRandomiserModules());
            }
            return factory.apply(thread);
        };
    }
}
