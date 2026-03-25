package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.client.render.item.model.special.ChestModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChestModelRenderer.class)
public interface ChestModelRendererAccessor {
    @Accessor @Mutable
    void setOpenness(float value);
}
