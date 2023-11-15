package com.bawnorton.trulyrandom.mixin.accessor;

import net.minecraft.loot.LootDataKey;
import net.minecraft.loot.LootManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(LootManager.class)
public interface LootManagerAccessor {
    @Accessor
    Map<LootDataKey<?>, ?> getKeyToValue();

    @Accessor
    void setKeyToValue(Map<LootDataKey<?>, ?> keyToValue);
}
