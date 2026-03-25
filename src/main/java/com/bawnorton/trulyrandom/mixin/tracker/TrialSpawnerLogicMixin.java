package com.bawnorton.trulyrandom.mixin.tracker;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.TeamMember;
import com.bawnorton.trulyrandom.mixin.accessor.TrialSpawnerDataAccessor;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.ResourceKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(TrialSpawnerLogic.class)
public abstract class TrialSpawnerLogicMixin {

    @Shadow public abstract TrialSpawnerData getData();

    @Inject(method = "ejectLootTable", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/ReloadableRegistries$Lookup;getLootTable(Lnet/minecraft/registry/ResourceKey;)Lnet/minecraft/loot/LootTable;"))
    private void trackCause(ServerWorld world, BlockPos pos, ResourceKey<LootTable> lootTable, CallbackInfo ci) {
        if(!TrulyRandom.getCachedRandomiser().getModules().isEnabled(Module.LOOT_TABLES)) return;

        LootTableTracker.LOOT_CAUSERS.set(((TrialSpawnerDataAccessor) getData()).getPlayers()
                .stream()
                .map(world::getPlayerByUuid)
                .filter(Objects::nonNull)
                .map(TeamMember::trulyrandom$getTeam)
                .collect(Collectors.toList()));
    }
}
