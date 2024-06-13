package com.bawnorton.trulyrandom.extend;

import net.minecraft.advancement.criterion.ItemCriterion;

public interface ItemCriterionCapture {
    ThreadLocal<ItemCriterion> TRIGGERD_CRITERION = new ThreadLocal<>();
}
