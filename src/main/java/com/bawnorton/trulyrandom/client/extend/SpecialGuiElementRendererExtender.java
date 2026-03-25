package com.bawnorton.trulyrandom.client.extend;

import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

public interface SpecialGuiElementRendererExtender<T extends SpecialGuiElementRenderState> {
    boolean trulyrandom$canBeReusedFor(T state, int width, int height);
}
