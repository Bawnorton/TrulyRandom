package com.bawnorton.trulyrandom.client.screen.render;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateElementRenderState(
        BlockState blockState,
        int x0,
        int x1,
        int y0,
        int y1,
        float scale,
        float angle,
        ScreenRectangle scissorArea,
        ScreenRectangle bounds
) implements PictureInPictureRenderState {
    public BlockStateElementRenderState(
            BlockState blockState,
            int x,
            int y,
            float scale,
            float angle,
            ScreenRectangle scissorArea
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
                PictureInPictureRenderState.getBounds(
                        (int) ((x - 16) * scale),
                        (int) ((y - 16) * scale),
                        (int) ((x + 16) * scale),
                        (int) ((y + 16) * scale),
                        scissorArea
                )
        );
    }

    @Override
    public int x0() {
        return x0;
    }

    @Override
    public int y0() {
        return y0;
    }
}