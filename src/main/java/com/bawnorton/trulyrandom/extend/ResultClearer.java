package com.bawnorton.trulyrandom.extend;

import net.minecraft.item.ItemStack;

public interface ResultClearer extends ResultSetter {
    default void trulyRandom$clearResult() {
        trulyRandom$setResult(ItemStack.EMPTY);
    }
}
