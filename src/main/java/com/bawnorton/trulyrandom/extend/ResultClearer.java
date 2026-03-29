package com.bawnorton.trulyrandom.extend;


import net.minecraft.world.item.ItemStack;

public interface ResultClearer extends ResultHolder {
    default void trulyrandom$clearResult() {
        trulyrandom$setResult(ItemStack.EMPTY);
    }
}
