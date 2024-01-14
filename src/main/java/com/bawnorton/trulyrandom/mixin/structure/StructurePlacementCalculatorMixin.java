package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.module.Module;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(StructurePlacementCalculator.class)
public abstract class StructurePlacementCalculatorMixin {
    @ModifyArg(method = "hasValidBiome", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"))
    private static <T> Predicate<? super T> setBiomeValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return (biome) -> {
            if (original.test(biome)) return true;
            return TrulyRandom.getUnsafeRandomiser().getModules().isEnabled(Module.STRUCTURES);
        };
    }

    @ModifyArg(method = "method_46711", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"))
    private <T> Predicate<? super T> allBiomesValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return (biome) -> {
            if (original.test(biome)) return true;
            return TrulyRandom.getUnsafeRandomiser().getModules().isEnabled(Module.STRUCTURES);
        };
    }
}
