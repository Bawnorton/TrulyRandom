package com.bawnorton.trulyrandom.mixin.feature;

import com.bawnorton.trulyrandom.random.feature.FeatureReplacementRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin {
    @WrapOperation(
            method = "applyBiomeDecoration",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/placement/PlacedFeature;placeWithBiomeCheck(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean randomiseFeatures(PlacedFeature instance, WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin, Operation<Boolean> original) {
        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        if (!modules.isEnabled(Module.FEATURES)) {
            return original.call(instance, level, generator, random, origin);
        } else {
            Registry<PlacedFeature> lookup = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
            FeatureReplacementRandomiser randomiser = worldGenHolder.featureReplacementRandomiser();
            Holder.Reference<PlacedFeature> randomFeature = lookup.getRandom(randomiser.getRandom(ChunkPos.containing(origin))).orElseThrow();
            PlacedFeature feature = randomFeature.unwrap().map(lookup::getValueOrThrow, Function.identity());
            return feature.place(level, generator, random, origin);
        }
    }
}
