//? if <=26.1.2 {
/*package com.bawnorton.trulyrandom.client.mixin.accessor;


import dev.kikugie.fletching_table.mixin.MixinEnvironment;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@MixinEnvironment("client")
@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Invoker("setSectionDirty")
    void trulyrandom$setSectionDirty(int sectionX, int sectionY, int sectionZ, boolean playerChanged);
}
*///?}