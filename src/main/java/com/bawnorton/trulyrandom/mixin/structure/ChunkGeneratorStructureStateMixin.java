package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;
import java.util.List;

@Mixin(ChunkGeneratorStructureState.class)
abstract class ChunkGeneratorStructureStateMixin {

    @Shadow
    public abstract List<Holder<StructureSet>> possibleStructureSets();

    @ModifyReturnValue(method = "getPlacementsForStructure", at = @At("RETURN"))
    private List<StructurePlacement> locateRandomisedStructures(List<StructurePlacement> original) {
        StructureModuleState state = RandomiserSaveLoader.getWorldGenHolder()
                .modules()
                .getState(Module.STRUCTURES, StructureModuleState.class);
        if (!state.isEnabled() || state.useLegacyRandomiser()) return original;
        return possibleStructureSets()
                .stream()
                .map(set -> set.value().placement())
                .distinct()
                .toList();
    }

    @ModifyArg(
            method = "hasBiomesForStructureSet",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private static <T> Predicate<? super T> setBiomeValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return trulyrandom$structureRandomiserPredicate(original);
    }

    @ModifyArg(
            method = "lambda$generatePositions$0",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/stream/Stream;anyMatch(Ljava/util/function/Predicate;)Z"
            )
    )
    private <T> Predicate<? super T> allBiomesValidIfStructureRandomiserEnabled(Predicate<? super T> original) {
        return trulyrandom$structureRandomiserPredicate(original);
    }

    @Unique
    private static <T> Predicate<? super T> trulyrandom$structureRandomiserPredicate(Predicate<? super T> original) {
        return (biome) -> {
            if (original.test(biome)) return true;
            WorldGenHolder holder = RandomiserSaveLoader.getWorldGenHolder();
            Modules modules = holder.modules();
            StructureModuleState state = modules.getState(Module.STRUCTURES, StructureModuleState.class);
            return state.isEnabled() && state.useLegacyRandomiser();
        };
    }
}
