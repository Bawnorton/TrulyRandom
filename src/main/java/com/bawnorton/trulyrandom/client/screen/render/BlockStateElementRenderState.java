package com.bawnorton.trulyrandom.client.screen.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;

public record BlockStateElementRenderState(
        BlockState blockState,
        int x1,
        int x2,
        int y1,
        int y2,
        float scale,
        float angle,
        ScreenRect scissorArea,
        ScreenRect bounds
) implements SpecialGuiElementRenderState {
    public BlockStateElementRenderState(
            BlockState blockState,
            int x,
            int y,
            float scale,
            float angle,
            ScreenRect scissorArea
    ) {
        this(
                blockState,
                (int) ((x - 16) * scale),
                (int) ((x + 16) * scale),
                (int) ((y - 16) * scale),
                (int) ((y + 16) * scale),
                scale,
                angle,
                scissorArea,
                SpecialGuiElementRenderState.createBounds(
                        (int) ((x - 16) * scale),
                        (int) ((y - 16) * scale),
                        (int) ((x + 16) * scale),
                        (int) ((y + 16) * scale),
                        scissorArea
                )
        );
    }
}