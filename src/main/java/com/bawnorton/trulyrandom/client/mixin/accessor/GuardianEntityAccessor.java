package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.entity.mob.GuardianEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuardianEntity.class)
public interface GuardianEntityAccessor {
    @Accessor
    void setTailAngle(float angle);
}
