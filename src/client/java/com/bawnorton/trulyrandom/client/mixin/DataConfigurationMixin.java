package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.DataConfigurationExtender;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.DataConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import java.util.Optional;
import java.util.function.Function;

@Mixin(DataConfiguration.class)
public abstract class DataConfigurationMixin implements DataConfigurationExtender {
    @Unique
    private Modules trulyrandom$randomiserModules;

    @ModifyArg(method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;",
                    remap = false
            )
    )
    private static Function<RecordCodecBuilder.Instance<DataConfiguration>, ? extends App<RecordCodecBuilder.Mu<DataConfiguration>, DataConfiguration>> attachRandomiserModules(Function<RecordCodecBuilder.Instance<DataConfiguration>, ? extends App<RecordCodecBuilder.Mu<DataConfiguration>, DataConfiguration>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                Modules.CODEC
                        .optionalFieldOf("trulyrandom$randomiserModules").xmap(optional -> optional.orElse(null), Optional::ofNullable)
                        .forGetter(dataConfig -> ((DataConfigurationExtender) (Object) dataConfig).trulyrandom$getRandomiserModules())
        ).apply(instance, (dataConfig, randomiser) -> {
            ((DataConfigurationExtender) (Object) dataConfig).trulyrandom$setRandomiserModules(randomiser);
            return dataConfig;
        });
    }

    @Override
    public Modules trulyrandom$getRandomiserModules() {
        if(trulyrandom$randomiserModules == null) {
            return RandomiserSaveLoader.getDefaultRandomiser();
        } else {
            return trulyrandom$randomiserModules;
        }
    }

    @Override
    public void trulyrandom$setRandomiserModules(Modules modules) {
        this.trulyrandom$randomiserModules = modules;
    }
}
