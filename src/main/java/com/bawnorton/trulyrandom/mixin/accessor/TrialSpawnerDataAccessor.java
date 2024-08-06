package com.bawnorton.trulyrandom.mixin.accessor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mixin(TrialSpawnerData.class)
public interface TrialSpawnerDataAccessor {
    @Accessor
    Set<UUID> getPlayers();

    @Invoker
    static Optional<Pair<PlayerEntity, RegistryEntry<StatusEffect>>> callFindPlayerWithOmen(ServerWorld world, List<UUID> players) {
        throw new AssertionError();
    }
}
