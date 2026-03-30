package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.mixin.accessor.TrialSpawnerDataAccessor;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(TrialSpawnerState.class)
abstract class TrialSpawnerStateMixin {
    @WrapOperation(
            method = "tickAndGetNext",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawnerState;spawnOminousOminousItemSpawner(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/trialspawner/TrialSpawner;)V"
            )
    )
    private void trackCause(TrialSpawnerState instance, ServerLevel level, BlockPos trialSpawnerPos, TrialSpawner trialSpawner, Operation<Void> original) {
        LootTableTracker.attachCause(
                () -> original.call(instance, level, trialSpawnerPos, trialSpawner),
                TrialSpawnerDataAccessor.trulyrandom$findPlayerWithOminousEffect(
                                level,
                                ((TrialSpawnerDataAccessor) trialSpawner.getStateData())
                                        .trulyrandom$detectedPlayers()
                                        .stream()
                                        .toList())
                        .map(Pair::getFirst)
                        .map(TeamMember::trulyrandom$getTeam)
                        .map(List::of)
                        .orElse(List.of())
        );
    }
}
