package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChunkAccess.class)
abstract class ChunkAccessMixin {
    @Shadow
    @Final
    protected ChunkPos chunkPos;

    @Shadow
    @Final
    protected LevelHeightAccessor levelHeightAccessor;

    @ModifyVariable(
            method = "getReferencesForStructure",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Structure useReplacement(Structure structure) {
        WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
        Modules modules = worldGenHolder.modules();
        StructureModuleState state = modules.getState(Module.STRUCTURES, StructureModuleState.class);
        if(state.isEnabled() && state.replaceStructuresInstead()) {
            if (levelHeightAccessor instanceof Level level) {
                return worldGenHolder.structureReplacementRandomiser().getReplacementFor(level.dimension(), chunkPos, structure);
            }
        }
        return structure;
    }
}
