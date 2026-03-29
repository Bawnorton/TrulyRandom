package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ReloadableServerRegistries.Holder.class)
abstract class ReloadableRegistries$LookupMixin implements LookupExtender {
    @Shadow public abstract LootTable getLootTable(ResourceKey<LootTable> key);

    @Shadow @Final private HolderLookup.Provider registries;

    @ModifyVariable(method = "getLootTable", at = @At("HEAD"), argsOnly = true)
    private @Nullable ResourceKey<LootTable> getRandomisedLootTable(@Nullable ResourceKey<LootTable> original) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) return original;

        return randomiser.getLootRandomiser().getLootTable(LootTableTracker.LOOT_CAUSERS.get(), original);
    }

    @Override
    public LootTable trulyrandom$getUnalteredLootTable(ResourceKey<LootTable> key) {
        return registries.lookup(Registries.LOOT_TABLE)
                .flatMap(registryEntryLookup -> registryEntryLookup.get(key))
                .map(Holder::value)
                .orElse(LootTable.EMPTY);
    }
}
