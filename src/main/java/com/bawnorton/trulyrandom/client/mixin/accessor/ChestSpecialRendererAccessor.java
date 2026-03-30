package com.bawnorton.trulyrandom.client.mixin.accessor;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@MixinEnvironment("client")
@Mixin(ChestSpecialRenderer.class)
public interface ChestSpecialRendererAccessor {
    @Accessor("openness") @Mutable
    void trulyrandom$openness(float value);
}
