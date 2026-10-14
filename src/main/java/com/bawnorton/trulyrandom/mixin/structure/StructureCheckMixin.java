package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.google.common.base.Predicates;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(StructureCheck.class)
abstract class StructureCheckMixin {
    //? if <=26.1.2 {
    /*@ModifyArg(
            method = "canCreateStructure",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;<init>(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)V"
            )
    )
    private Predicate<Holder<Biome>> allowAnyBiome(Predicate<Holder<Biome>> original) {
        return trulyrandom$biomePredicate(original);
    }
    *///?} else {
    @ModifyArg(
            method = "canCreateStructure",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;<init>(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/world/level/biome/BiomeSource;Lnet/minecraft/world/level/biome/Climate$Sampler;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;JLnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/LevelHeightAccessor;Ljava/util/function/Predicate;)V"
            )
    )
    private Predicate<Holder<Biome>> allowAnyBiome(Predicate<Holder<Biome>> original) {
        return trulyrandom$biomePredicate(original);
    }
    //?}
    @Unique
    private Predicate<Holder<Biome>> trulyrandom$biomePredicate(Predicate<Holder<Biome>> original) {
        StructureModuleState state = RandomiserSaveLoader.getWorldGenHolder()
                .modules()
                .getState(Module.STRUCTURES, StructureModuleState.class);
        return state.isEnabled() && !state.useLegacyRandomiser() ? Predicates.alwaysTrue() : original;
    }
}
