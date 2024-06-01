package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.tracker.Team;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

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
        if(!team.getOwner().equals(getUuid())) {
            trulyrandom$team.addPlayer(getUuid());
        }
    }

    @Override
    public void trulyrandom$leaveTeam() {
        if (trulyrandom$team == null) return;

        trulyrandom$team.removePlayer(getUuid());
        trulyrandom$team = Team.create(getUuid());
    }

    @Override
    public @NotNull Team trulyrandom$getTeam() {
        trulyrandom$team = trulyrandom$team == null ? Team.create(getUuid()) : trulyrandom$team;
        return trulyrandom$team;
    }
}
