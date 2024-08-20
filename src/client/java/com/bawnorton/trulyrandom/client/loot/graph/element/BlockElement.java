package com.bawnorton.trulyrandom.client.loot.graph.element;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.screen.BlockStateGuiRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class BlockElement extends GraphElement implements BlockStateGuiRenderer {
    private final Block block;

    public BlockElement(Block block) {
        this.block = block;
    }

    @Override
    public void render(DrawContext context, MinecraftClient client, int x, int y, float scale) {
        render(context, client, x, y, 1, block.getDefaultState());
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
        return "BlockElement[%s]".formatted(Registries.BLOCK.getId(block));
    }
}
