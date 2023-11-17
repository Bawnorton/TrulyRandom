package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.random.Module;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Predicate;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {
    @ModifyArg(method = "trySetStructureStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/Structure;createStructureStart(Lnet/minecraft/registry/DynamicRegistryManager;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/source/BiomeSource;Lnet/minecraft/world/gen/noise/NoiseConfig;Lnet/minecraft/structure/StructureTemplateManager;JLnet/minecraft/util/math/ChunkPos;ILnet/minecraft/world/HeightLimitView;Ljava/util/function/Predicate;)Lnet/minecraft/structure/StructureStart;"))
    private Predicate<RegistryEntry<Biome>> allBiomesValidIfStructureRandomiserEnabled(Predicate<RegistryEntry<Biome>> original) {
        if(TrulyRandom.getUnsafeRandomiser().getModules().isEnabled(Module.STRUCTURES)) return (biome) -> true;
        return original;
    }
}
