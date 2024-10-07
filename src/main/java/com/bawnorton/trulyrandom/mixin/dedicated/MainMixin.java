package com.bawnorton.trulyrandom.mixin.dedicated;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Main.class)
public abstract class MainMixin {
    @ModifyReceiver(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Ljoptsimple/OptionParser;parse([Ljava/lang/String;)Ljoptsimple/OptionSet;"
            ),
            remap = false
    )
    private static OptionParser addTrulyRandomOptions(OptionParser instance, String[] arguments, @Share("modules") LocalRef<Map<Module, OptionSpec<Long>>> modules) {
        Map<Module, OptionSpec<Long>> moduleOptions = new HashMap<>();
        for (Module module : Module.values()) {
            OptionSpec<Long> spec = instance.accepts(module.name().toLowerCase())
                    .withOptionalArg()
                    .ofType(Long.class);
            moduleOptions.put(module, spec);
        }
        modules.set(moduleOptions);
        return instance;
    }

    @Inject(
            method = "main",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/Main;createServerConfig(Lnet/minecraft/server/dedicated/ServerPropertiesHandler;Lcom/mojang/serialization/Dynamic;ZLnet/minecraft/resource/ResourcePackManager;)Lnet/minecraft/server/SaveLoading$ServerConfig;"
            )
    )
    private static void loadSpecifiedRandomiser(CallbackInfo ci, @Local OptionSet optionSet, @Share("modules") LocalRef<Map<Module, OptionSpec<Long>>> modules) {
        Map<Module, OptionSpec<Long>> moduleOptions = modules.get();
        Modules newModules = new Modules();
        List<?> args = optionSet.valuesOf("[arguments]");
        for(Module module : Module.values()) {
            OptionSpec<Long> spec = moduleOptions.get(module);
            String key = spec.options().getFirst();
            int index = args.indexOf(key);
            if (index == -1) continue;

            newModules.setEnabled(module);
            Long seed = null;
            if(index + 1 < args.size()) {
                String next = (String) args.get(index + 1);
                try {
                    seed = Long.parseLong(next);
                } catch (NumberFormatException ignored) {}
            }
            if(seed != null) {
                newModules.setSeed(module, seed);
            }
        }
        TrulyRandom.setLoadedRandomiser(newModules);
    }
}
