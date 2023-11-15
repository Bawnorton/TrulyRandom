package com.bawnorton.trulyrandom.client.mixin.accessor;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererInvoker {
    @Invoker
    void invokeScheduleChunkRender(int x, int y, int z, boolean important);
}
