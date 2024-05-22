package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.block.spawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Set;
import java.util.UUID;

@Mixin(TrialSpawnerData.class)
public interface TrialSpawnerDataAccessor {
    @Accessor
    Set<UUID> getPlayers();
}
