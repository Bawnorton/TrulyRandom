package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.extend.ModulesHolder;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.level.WorldDataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.Optional;
import java.util.function.Function;

@MixinEnvironment("client")
@Mixin(WorldDataConfiguration.class)
abstract class DataConfigurationMixin implements ModulesHolder {
    @Unique
    private Modules trulyrandom$randomiserModules;

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;mapCodec(Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;",
                    remap = false
            )
    )
    private static Function<RecordCodecBuilder.Instance<WorldDataConfiguration>, ? extends App<RecordCodecBuilder.Mu<WorldDataConfiguration>, WorldDataConfiguration>> attachRandomiserModules(Function<RecordCodecBuilder.Instance<WorldDataConfiguration>, ? extends App<RecordCodecBuilder.Mu<WorldDataConfiguration>, WorldDataConfiguration>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Modules.CODEC
                        .optionalFieldOf("trulyrandom$randomiserModules").xmap(optional -> optional.orElse(new Modules()), Optional::ofNullable)
                        .forGetter(dataConfig -> ((ModulesHolder) (Object) dataConfig).trulyrandom$getRandomiserModules())
        ).apply(instance, (dataConfig, modules) -> {
            ((ModulesHolder) (Object) dataConfig).trulyrandom$setRandomiserModules(modules);
            return dataConfig;
        });
    }

    @Override
    public Modules trulyrandom$getRandomiserModules() {
        return trulyrandom$randomiserModules;
    }

    @Override
    public void trulyrandom$setRandomiserModules(Modules modules) {
        this.trulyrandom$randomiserModules = modules;
    }
}
