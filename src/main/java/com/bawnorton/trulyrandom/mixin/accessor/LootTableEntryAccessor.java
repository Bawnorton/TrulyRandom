package com.bawnorton.trulyrandom.mixin.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.registry.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootTableEntry.class)
public interface LootTableEntryAccessor {
    @Accessor
    Either<ResourceKey<LootTable>, LootTable> getValue();
}
