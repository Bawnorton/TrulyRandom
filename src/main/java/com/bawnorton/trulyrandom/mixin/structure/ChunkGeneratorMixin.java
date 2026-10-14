package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.google.common.base.Predicates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin {
    @WrapOperation(
            method = "tryGenerateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/StructureSet$StructureSelectionEntry;structure()Lnet/minecraft/core/Holder;"
            )
    )
    private Holder<Structure> randomiseStructure(StructureSet.StructureSelectionEntry instance, Operation<Holder<Structure>> original,
                                                 @Local(argsOnly = true) RegistryAccess registryAccess,
                                                 @Local(argsOnly = true) ChunkPos sourceChunkPos,
                                                 @Local(argsOnly = true) ResourceKey<Level> level
    ) {
        Holder<Structure> source = original.call(instance);
        WorldGenHolder holder = RandomiserSaveLoader.getWorldGenHolder();
        StructureModuleState state = holder.modules().getState(Module.STRUCTURES, StructureModuleState.class);
        if (!state.isEnabled() || state.useLegacyRandomiser()) return source;

        return holder.structureRandomiser()
                .getReplacement(registryAccess.lookupOrThrow(Registries.STRUCTURE), source, level, sourceChunkPos);
    }

    //? if <=26.1.2 {
    /*@ModifyArg(
            method = "tryGenerateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure;generate(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
            )
    )
    private Predicate<Holder<Biome>> allowAnyBiome(Predicate<Holder<Biome>> original) {
        return trulyrandom$generationBiomePredicate(original);
    }
    *///?} else {
    @ModifyArg(
            method = "tryGenerateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure;generate(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/biome/Climate$Sampler;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
            )
    )
    private Predicate<Holder<Biome>> allowAnyBiome(Predicate<Holder<Biome>> original) {
        return trulyrandom$generationBiomePredicate(original);
    }
    //?}

    @Unique
    private Predicate<Holder<Biome>> trulyrandom$generationBiomePredicate(Predicate<Holder<Biome>> original) {
        StructureModuleState state = RandomiserSaveLoader.getWorldGenHolder()
                .modules()
                .getState(Module.STRUCTURES, StructureModuleState.class);
        return state.isEnabled() ? Predicates.alwaysTrue() : original;
    }
}
