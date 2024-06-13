package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ReloadableRegistries.Lookup.class)
public abstract class ReloadableRegistries$LookupMixin implements LookupExtender {
    @Shadow public abstract LootTable getLootTable(RegistryKey<LootTable> key);

    @Shadow @Final private DynamicRegistryManager.Immutable registryManager;

    @ModifyVariable(method = "getLootTable", at = @At("HEAD"), argsOnly = true)
    private @Nullable RegistryKey<LootTable> getRandomisedLootTable(@Nullable RegistryKey<LootTable> original) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) return original;

        return randomiser.getLootRandomiser().getLootTable(LootTableTracker.LOOT_CAUSERS.get(), original);
    }

    @Override
    public LootTable trulyrandom$getUnalteredLootTable(RegistryKey<LootTable> key) {
        return registryManager
                .getOptionalWrapper(RegistryKeys.LOOT_TABLE)
                .flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key))
                .map(RegistryEntry::value)
                .orElse(LootTable.EMPTY);
    }
}
