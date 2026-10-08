package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CompositeLootItemCondition.class)
public interface CompositeLootItemConditionAccessor {
    //? if >=26.3 {
    @Accessor("terms")
    HolderSet<LootItemCondition> trulyrandom$terms();
    //?} else {
    /*@Accessor("terms")
    List<LootItemCondition> trulyrandom$terms();
    *///?}
}
