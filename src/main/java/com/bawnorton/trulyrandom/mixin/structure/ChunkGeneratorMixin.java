package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.random.structure.StructureRandomiser;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.google.common.base.Predicates;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;
import java.util.function.Predicate;

@Mixin(ChunkGenerator.class)
abstract class ChunkGeneratorMixin {
    @WrapOperation(
            method = "tryGenerateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure;generate(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;ILnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)Lnet/minecraft/world/level/levelgen/structure/StructureStart;"
            )
    )
    private StructureStart randomiseStructurePlacement(Structure instance, Holder<Structure> selected, ResourceKey<Level> dimension, RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, RandomState randomState, StructureTemplateManager structureTemplateManager, long seed, ChunkPos sourceChunkPos, int references, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> validBiome, Operation<StructureStart> original) {
        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        StructureModuleState state = modules.getState(Module.STRUCTURES, StructureModuleState.class);
        if(state.isEnabled()) {
            if (state.useLegacyRandomiser()) {
                return original.call(instance, selected, dimension, registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, sourceChunkPos, references, heightAccessor, Predicates.alwaysTrue());
            } else {
                if (instance.type().equals(StructureType.STRONGHOLD)) {
                    return original.call(instance, selected, dimension, registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, sourceChunkPos, references, heightAccessor, validBiome);
                }
                Registry<Structure> lookup = registryAccess.lookupOrThrow(Registries.STRUCTURE);
                StructureRandomiser randomiser = worldGenHolder.structureRandomiser();
                Holder.Reference<Structure> randomStructure = lookup.getRandom(randomiser.getRandom(dimension, sourceChunkPos)).orElseThrow();
                Structure structure = randomStructure.unwrap().map(lookup::getValueOrThrow, Function.identity());
                while (structure.type().equals(StructureType.STRONGHOLD)) {
                    randomStructure = lookup.getRandom(randomiser.getRandom(dimension, sourceChunkPos)).orElseThrow();
                    structure = randomStructure.unwrap().map(lookup::getValueOrThrow, Function.identity());
                }
                StructureStart start = original.call(structure, selected, dimension, registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, sourceChunkPos, references, heightAccessor, Predicates.alwaysTrue());
                randomiser.registerReplacement(dimension, sourceChunkPos, instance, structure);
                for (StructurePiece piece : start.getPieces()) {
                    BoundingBox boundingBox = piece.getBoundingBox();
                    ChunkPos minXZ = new ChunkPos(
                            SectionPos.blockToSectionCoord(boundingBox.minX()),
                            SectionPos.blockToSectionCoord(boundingBox.minZ())
                    );
                    ChunkPos maxXZ = new ChunkPos(
                            SectionPos.blockToSectionCoord(boundingBox.maxX()),
                            SectionPos.blockToSectionCoord(boundingBox.maxZ())
                    );
                    for (int i = minXZ.x(); i <= maxXZ.x(); i++) {
                        for (int j = minXZ.z(); j <= maxXZ.z(); j++) {
                            randomiser.registerReplacement(dimension, new ChunkPos(i, j), instance, structure);
                        }
                    }
                }
                return start;
            }
        }
        return original.call(instance, selected, dimension, registryAccess, chunkGenerator, biomeSource, randomState, structureTemplateManager, seed, sourceChunkPos, references, heightAccessor, validBiome);
    }
}
