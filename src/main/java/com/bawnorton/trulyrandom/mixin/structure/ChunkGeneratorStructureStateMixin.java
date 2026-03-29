package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ChunkGeneratorStructureState.class)
abstract class ChunkGeneratorStructureStateMixin {
    @ModifyArg(
            method = "hasBiomesForStructureSet",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private static <T> Predicate<? super T> setBiomeValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return (biome) -> {
            if (original.test(biome)) return true;
            return RandomiserSaveLoader.getWorldGenModules().isEnabled(Module.STRUCTURES);
        };
    }

    @ModifyArg(
            method = "lambda$generatePositions$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private <T> Predicate<? super T> allBiomesValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return (biome) -> {
            if (original.test(biome)) return true;
            return RandomiserSaveLoader.getWorldGenModules().isEnabled(Module.STRUCTURES);
        };
    }
}
