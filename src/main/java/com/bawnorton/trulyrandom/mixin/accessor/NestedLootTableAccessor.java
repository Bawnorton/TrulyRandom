package com.bawnorton.trulyrandom.mixin.accessor;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NestedLootTable.class)
public interface NestedLootTableAccessor {
    //? if <=26.1.2 {
    /*@Accessor("contents")
    Either<ResourceKey<LootTable>, LootTable> trulyrandom$contents();
    *///?} else {
    @Accessor("value")
    HolderSet<LootTable> trulyrandom$value();
    //?}
}
