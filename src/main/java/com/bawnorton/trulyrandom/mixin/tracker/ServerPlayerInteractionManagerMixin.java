package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayer;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow @Final protected ServerPlayer player;

    @WrapOperation(
            method = "interactBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"
            )
    )
    private ActionResult trackCause(BlockState instance, World world, PlayerEntity player, BlockHitResult blockHitResult, Operation<ActionResult> original) {
        return LootTableTracker.attachCause(() -> original.call(instance, world, player, blockHitResult), List.of(player.trulyrandom$getTeam()));
    }

    @WrapOperation(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
            )
    )
    private boolean trackCause(ServerWorld instance, BlockPos pos, boolean b, Operation<Boolean> original) {
        return LootTableTracker.attachCause(() -> original.call(instance, pos, b), List.of(((TeamMember) player).trulyrandom$getTeam()));
    }
}
