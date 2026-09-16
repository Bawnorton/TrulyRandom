package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.network.packet.clientbound.ClientboundChangeTeamPacket;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.team.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.bawnorton.trulyrandom.tracker.recipe.RecipeTracker;
import com.bawnorton.trulyrandom.tracker.trade.TradeTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
    public @NotNull Team trulyrandom$getTeam() {
        if (this.level() instanceof ServerLevel level) {
            trulyrandom$team = trulyrandom$team == null ? TrulyRandom.getTeams(level.getServer()).getOrCreate(getUUID()) : trulyrandom$team;
        } else if (trulyrandom$team == null) {
            return Team.create(getUUID());
        }
        return trulyrandom$team;
    }

    @Override
    public void trulyrandom$setTeam(Team team) {
        trulyrandom$team = team;

        if (this.level() instanceof ServerLevel level) {
            ServerRandomiser randomiser = TrulyRandom.getRandomiser(level.getServer());

            Team currentTeam = trulyrandom$getTeam();
            LootTableTracker lootTableTracker = randomiser.getLootRandomiser().getTracker(currentTeam);
            RecipeTracker recipeTracker = randomiser.getRecipeRandomiser().getTracker(currentTeam);
            TradeTracker tradeTracker = randomiser.getTradeRandomiser().getTracker(currentTeam);

            if (lootTableTracker != null) {
                lootTableTracker.markDirty();
            }

            if (recipeTracker != null) {
                recipeTracker.markDirty();
            }

            if (tradeTracker != null) {
                tradeTracker.markDirty();
            }

            if ((Object) this instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new ClientboundChangeTeamPacket(currentTeam));
            }
        }
    }

    @WrapOperation(
            method = "interactOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/InteractionResult;"
            )
    )
    private InteractionResult trackCause(Entity instance, Player player, InteractionHand hand, Vec3 location, Operation<InteractionResult> original) {
        LootTableTracker.LOOT_CAUSERS.set(List.of(trulyrandom$getTeam()));
        InteractionResult result = original.call(instance, player, hand, location);
        LootTableTracker.LOOT_CAUSERS.remove();
        return result;
    }
}
