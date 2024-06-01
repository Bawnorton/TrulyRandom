package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.DataConfigurationExtender;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.featuretoggle.FeatureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.function.Function;

@Mixin(DataConfiguration.class)
public abstract class DataConfigurationMixin implements DataConfigurationExtender {
    @Unique
    private static final ThreadLocal<ServerRandomiser> trulyrandom$randomiserThreadLocal = ThreadLocal.withInitial(() -> ServerRandomiser.DEFAULT);
    @Unique
    private ServerRandomiser trulyrandom$randomiser;

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;", remap = false))
    private static Function<RecordCodecBuilder.Instance<DataConfiguration>, ? extends App<RecordCodecBuilder.Mu<DataConfiguration>, DataConfiguration>> attachRandomiserModules(Function<RecordCodecBuilder.Instance<DataConfiguration>, ? extends App<RecordCodecBuilder.Mu<DataConfiguration>, DataConfiguration>> builder) {
        return instance -> instance.group(
                RecordCodecBuilder.mapCodec(builder).forGetter(Function.identity()),
                ServerRandomiser.CODEC
                        .optionalFieldOf("trulyrandom$randomiser", ServerRandomiser.DEFAULT)
                        .forGetter(dataConfig -> ((DataConfigurationExtender) (Object) dataConfig).trulyrandom$getRandomiser())
        ).apply(instance, (dataConfig, randomiser) -> {
            trulyrandom$randomiserThreadLocal.set((ServerRandomiser) randomiser);
            return dataConfig;
        });
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void attachRandomiserData(DataPackSettings dataPackSettings, FeatureSet featureSet, CallbackInfo ci) {
        //noinspection ConstantValue
        if (trulyrandom$randomiserThreadLocal == null) {
            this.trulyrandom$randomiser = ServerRandomiser.DEFAULT;
        } else {
            this.trulyrandom$randomiser = trulyrandom$randomiserThreadLocal.get();
        }
    }

    @Override
    public Randomiser trulyrandom$getRandomiser() {
        return trulyrandom$randomiser;
    }
}
