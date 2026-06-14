package com.bawnorton.trulyrandom.mixin.structure;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.random.module.state.StructureModuleState;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatusTasks.class)
abstract class ChunkStatusTaskMixin {
    @ModifyReturnValue(
            method = "full",
            at = @At("RETURN")
    )
    private static CompletableFuture<ChunkAccess> clearStructureModule(CompletableFuture<ChunkAccess> original, WorldGenContext context) {
        return original.whenComplete((chunk, _) -> {
            WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
            Modules modules = worldGenHolder.modules();
            StructureModuleState state = modules.getState(Module.STRUCTURES, StructureModuleState.class);
            if (state.isEnabled() && state.replaceStructuresInstead()) {
                Level level = context.level();
                worldGenHolder.structureReplacementRandomiser().clearChunk(level.dimension(), chunk.getPos());
            }
        });
    }
}
