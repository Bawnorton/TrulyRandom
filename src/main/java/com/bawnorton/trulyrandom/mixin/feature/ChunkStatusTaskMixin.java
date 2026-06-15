package com.bawnorton.trulyrandom.mixin.feature;

import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.random.module.Modules;
import com.bawnorton.trulyrandom.world.RandomiserSaveLoader;
import com.bawnorton.trulyrandom.world.WorldGenHolder;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
    private static CompletableFuture<ChunkAccess> clearFeatureModule(CompletableFuture<ChunkAccess> original, WorldGenContext context) {
        return original.whenComplete((chunk, _) -> {
            WorldGenHolder worldGenHolder = RandomiserSaveLoader.getWorldGenHolder();
            Modules modules = worldGenHolder.modules();
            if (modules.isEnabled(Module.FEATURES)) {
                worldGenHolder.featureRandomiser().clearChunk(chunk.getPos());
            }
        });
    }
}
