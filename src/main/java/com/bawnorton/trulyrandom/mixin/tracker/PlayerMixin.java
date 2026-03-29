package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import java.util.List;

@Mixin(Player.class)
abstract class PlayerMixin extends LivingEntity implements TeamMember {
    @Unique
    private @Nullable Team trulyrandom$team;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
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
            method = "interactOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;"
            )
    )
    private InteractionResult trackCause(Entity instance, Player mob, InteractionHand anyLeashed, Vec3 mobsToLeash, Operation<InteractionResult> original) {
        LootTableTracker.LOOT_CAUSERS.set(List.of(trulyrandom$getTeam()));
        InteractionResult result = original.call(instance, mob, anyLeashed, mobsToLeash);
        LootTableTracker.LOOT_CAUSERS.remove();
        return result;
    }
}
