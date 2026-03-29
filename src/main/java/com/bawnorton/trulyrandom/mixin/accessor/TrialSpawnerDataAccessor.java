package com.bawnorton.trulyrandom.mixin.accessor;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerStateData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mixin(TrialSpawnerStateData.class)
public interface TrialSpawnerDataAccessor {
    @Accessor("detectedPlayers")
    Set<UUID> trulyrandom$detectedPlayers();

    @Invoker("findPlayerWithOminousEffect")
    static Optional<Pair<Player, Holder<MobEffect>>> trulyrandom$findPlayerWithOminousEffect(final ServerLevel level, final List<UUID> inLineOfSightPlayers) {
        throw new AssertionError();
    }
}
