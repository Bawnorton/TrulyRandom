package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.entry.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntry.class)
public interface ItemEntryAccessor extends LootPoolEntryAccessor {
    @Accessor
    Holder<Item> getItem();
}
