package com.bawnorton.trulyrandom.mixin.loot;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.extend.LookupExtender;
import com.bawnorton.trulyrandom.random.ServerRandomiser;
import com.bawnorton.trulyrandom.random.module.Module;
import com.bawnorton.trulyrandom.tracker.loot.LootTableTracker;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.ResourceKey;
import net.minecraft.registry.BuiltInRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.registry.entry.Holder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ReloadableRegistries.Lookup.class)
public abstract class ReloadableRegistries$LookupMixin implements LookupExtender {
    @Shadow public abstract LootTable getLootTable(ResourceKey<LootTable> key);

    @Shadow @Final private RegistryWrapper.WrapperLookup registries;

    @ModifyVariable(method = "getLootTable", at = @At("HEAD"), argsOnly = true)
    private @Nullable ResourceKey<LootTable> getRandomisedLootTable(@Nullable ResourceKey<LootTable> original) {
        ServerRandomiser randomiser = TrulyRandom.getCachedRandomiser();
        if(!randomiser.getModules().isEnabled(Module.LOOT_TABLES)) return original;

        return randomiser.getLootRandomiser().getLootTable(LootTableTracker.LOOT_CAUSERS.get(), original);
    }

    @Override
    public LootTable trulyrandom$getUnalteredLootTable(ResourceKey<LootTable> key) {
        return registries
                .getOptional(BuiltInRegistries.LOOT_TABLE)
                .flatMap(registryEntryLookup -> registryEntryLookup.getOptional(key))
                .map(Holder::value)
                .orElse(LootTable.EMPTY);
    }
}
