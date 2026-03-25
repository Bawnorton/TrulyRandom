package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.entity.passive.CatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CatEntity.class)
public interface CatEntityAccessor {
    @Accessor
    void setSleepAnimation(float amount);
}
