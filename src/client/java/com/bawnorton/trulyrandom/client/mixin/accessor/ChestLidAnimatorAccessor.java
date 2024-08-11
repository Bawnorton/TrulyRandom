package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestLidAnimator.class)
public interface ChestLidAnimatorAccessor {
    @Accessor
    void setProgress(float progress);

    @Accessor
    void setLastProgress(float lastProgress);

    @Accessor
    void setOpen(boolean open);
}
