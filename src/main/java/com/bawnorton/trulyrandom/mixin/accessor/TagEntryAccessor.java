package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TagEntry.class)
public interface TagEntryAccessor {
    //? if <=26.1.2 {
    /*@Accessor("tag")
    TagKey<Item> trulyrandom$tag();
    *///?} else {
    @Accessor("tag")
    HolderSet<Item> trulyrandom$tag();
    //?}
}
