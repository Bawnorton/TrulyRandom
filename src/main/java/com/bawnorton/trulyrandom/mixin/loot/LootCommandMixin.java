package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.team.Team;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(LootCommand.class)
abstract class LootCommandMixin {
    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "drop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;"
            )
    )
    private static <T> T replaceLootTable(Holder<T> instance, Operation<T> original, CommandContext<CommandSourceStack> context) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) {
            return original.call(instance);
        }

        ServerPlayer player = context.getSource().getPlayer();
        List<Team> teams = player == null ? List.of() : List.of(((TeamMember) player).trulyrandom$getTeam());
        return (T) randomiser.getLootRandomiser().getLootTable(teams, (Holder<LootTable>) instance).value();
    }
}
