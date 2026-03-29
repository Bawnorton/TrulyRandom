package com.bawnorton.trulyrandom.extend;


import net.minecraft.advancements.criterion.ItemUsedOnLocationTrigger;

public interface ItemUsedOnLocationTriggerCapture {
    ThreadLocal<ItemUsedOnLocationTrigger> TRIGGERD_CRITERION = new ThreadLocal<>();
}
