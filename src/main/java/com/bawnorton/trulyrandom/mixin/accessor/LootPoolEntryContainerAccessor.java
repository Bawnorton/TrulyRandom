package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.List;
import java.util.Optional;

@Mixin(LootPoolEntryContainer.class)
public interface LootPoolEntryContainerAccessor {
    //? if <=26.1.2 {
    /*@Accessor("conditions")
    List<LootItemCondition> trulyrandom$conditions();
    *///?} else {
    @Accessor("condition")
    Optional<Holder<LootItemCondition>> trulyrandom$condition();
    //?}
}
