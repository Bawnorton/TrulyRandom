package com.bawnorton.trulyrandom.client.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.jungrapht.visualization.layout.algorithms.eiglsperger.EiglspergerSteps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MixinEnvironment("client")
@Mixin(EiglspergerSteps.class)
abstract class EiglspergerStepsMixin {
    @Inject(
            method = {
                    "log(Ljava/lang/String;[Lorg/jungrapht/visualization/layout/algorithms/sugiyama/LV;)V",
                    "log(Ljava/lang/String;Ljava/util/List;)V"
            },
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void pleaseStopSpammingTheLog(CallbackInfo ci) {
        ci.cancel();
    }
}
