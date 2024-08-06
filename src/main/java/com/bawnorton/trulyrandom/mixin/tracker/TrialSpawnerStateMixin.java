package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.mixin.accessor.TrialSpawnerDataAccessor;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(TrialSpawnerState.class)
public abstract class TrialSpawnerStateMixin {
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/enums/TrialSpawnerState;spawnOminousItemSpawner(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/spawner/TrialSpawnerLogic;)V"
            )
    )
    private void trackCause(TrialSpawnerState instance, ServerWorld world, BlockPos pos, TrialSpawnerLogic logic, Operation<Void> original) {
        LootTableTracker.attachCause(
                () -> original.call(instance, world, pos, logic),
                TrialSpawnerDataAccessor.callFindPlayerWithOmen(
                                world,
                                ((TrialSpawnerDataAccessor) logic.getData())
                                        .getPlayers()
                                        .stream()
                                        .toList())
                        .map(Pair::getFirst)
                        .map(TeamMember::trulyrandom$getTeam)
                        .map(List::of)
                        .orElse(List.of())
        );
    }
}
