package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.client.screen.render.BlockStateElementRenderState;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.BuiltInRegistries;
import net.minecraft.text.Text;

public class BlockElement extends GraphElement {
    private final Block block;

    public BlockElement(Block block) {
        this.block = block;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale) {
        context.state.addSpecialElement(new BlockStateElementRenderState(
                block.getDefaultState(), x, y, scale, 45, context.scissorStack.peekLast()
        ));
    }

    @Override
    protected Text getTooltip() {
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
        return "BlockElement[%s]".formatted(BuiltInRegistries.BLOCK.getId(block));
    }
}
