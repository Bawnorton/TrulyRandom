package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.render.BlockStateElementRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class BlockElement extends GraphElement {
    private final Block block;

    public BlockElement(Block block) {
        this.block = block;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Minecraft minecraft, int mouseX, int mouseY, int x, int y, float scale) {
        graphics.guiRenderState.addPicturesInPictureState(new BlockStateElementRenderState(
                block.defaultBlockState(), x, y, scale, 45, graphics.scissorStack.peek()
        ));
    }

    @Override
    protected Component getTooltip() {
        return block.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BlockElement blockElement) {
            return block.equals(blockElement.block);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return block.hashCode();
    }

    @Override
    public String toString() {
        return "BlockElement[%s]".formatted(BuiltInRegistries.BLOCK.getKey(block));
    }
}
