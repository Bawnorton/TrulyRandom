package com.bawnorton.trulyrandom.client.mixin;

import com.bawnorton.trulyrandom.client.extend.DataConfigurationExtender;
import com.bawnorton.trulyrandom.random.Randomiser;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.featuretoggle.FeatureFlags;
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
    private Randomiser randomiser;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"))
    private static <O> Function<RecordCodecBuilder.Instance, ?> attachRandomiserModules(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> builder) {
        return instance -> instance.group(
                DataPackSettings.CODEC.optionalFieldOf("DataPacks", DataPackSettings.SAFE_MODE).forGetter(DataConfiguration::dataPacks),
                FeatureFlags.CODEC.optionalFieldOf("enabled_features", FeatureFlags.DEFAULT_ENABLED_FEATURES).forGetter(DataConfiguration::enabledFeatures),
                Randomiser.CODEC.optionalFieldOf("randomiser", new Randomiser()).forGetter(DataConfigurationExtender::trulyRandom$getRandomiser)
        ).apply(instance, (a, b, c) -> {
            randomiserThreadLocal.set((Randomiser) c);
            return new DataConfiguration((DataPackSettings) a, (FeatureSet) b);
        });
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void attachRandomiserData(DataPackSettings dataPackSettings, FeatureSet featureSet, CallbackInfo ci) {
        this.randomiser = randomiserThreadLocal.get();
    }

    @Override
    public Randomiser trulyRandom$getRandomiser() {
        return randomiser;
    }
}
