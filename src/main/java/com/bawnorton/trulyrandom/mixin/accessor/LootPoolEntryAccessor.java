package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.loot.condition.LootItemCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;

@Mixin(LootPoolEntry.class)
public interface LootPoolEntryAccessor {
    @Accessor
    List<LootItemCondition> getConditions();
}
