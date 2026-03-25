package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements TeamMember {
    @Unique
    private @Nullable Team trulyrandom$team;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void trulyrandom$joinTeam(@NotNull Team team) {
        trulyrandom$team = team;
        if(!team.getOwner().equals(getUUID())) {
            trulyrandom$team.addPlayer(getUUID());
        }
    }

    @Override
    public void trulyrandom$leaveTeam() {
        if (trulyrandom$team == null) return;

        trulyrandom$team.removePlayer(getUUID());
        trulyrandom$team = Team.create(getUUID());
    }

    @Override
    public @NotNull Team trulyrandom$getTeam() {
        trulyrandom$team = trulyrandom$team == null ? Team.create(getUUID()) : trulyrandom$team;
        return trulyrandom$team;
    }

    @WrapOperation(
            method = "interact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"
            )
    )
    private ActionResult trackCause(Entity instance, PlayerEntity player, Hand hand, Operation<ActionResult> original) {
        LootTableTracker.LOOT_CAUSERS.set(List.of(trulyrandom$getTeam()));
        ActionResult result = original.call(instance, player, hand);
        LootTableTracker.LOOT_CAUSERS.remove();
        return result;
    }
}
