package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.team.Team;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(AdvancementRewards.class)
abstract class AdvancementRewardsMixin {
    //? if >=26.3 {
    @SuppressWarnings("unchecked")
    @WrapOperation(
            method = "grant",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/Holder;value()Ljava/lang/Object;"
            )
    )
    private static <T> T replaceLootTable(Holder<T> instance, Operation<T> original, ServerPlayer player) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) {
            return original.call(instance);
        }

        List<Team> teams = List.of(((TeamMember) player).trulyrandom$getTeam());
        return (T) randomiser.getLootRandomiser().getLootTable(teams, (Holder<LootTable>) instance).value();
    }
    //?} else {
    /*@WrapOperation(
            method = "grant",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/ReloadableServerRegistries$Holder;getLootTable(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/world/level/storage/loot/LootTable;"
            )
    )
    private static LootTable replaceLootTable(ReloadableServerRegistries.Holder instance, ResourceKey<LootTable> id, Operation<LootTable> original, ServerPlayer player) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) {
            return original.call(instance, id);
        }

        List<Team> teams = List.of(((TeamMember) player).trulyrandom$getTeam());
        return original.call(instance, randomiser.getLootRandomiser().getLootTable(teams, id));
    }
    *///?}
}
