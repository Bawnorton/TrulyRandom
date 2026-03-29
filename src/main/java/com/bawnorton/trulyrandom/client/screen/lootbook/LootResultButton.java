package com.bawnorton.trulyrandom.client.screen.lootbook;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LootResultButton extends AbstractWidget {
    private static final Identifier SLOT_TEXTURE = Identifier.withDefaultNamespace("recipe_book/slot_craftable");
    public static final int BUTTON_SIZE = 25;
    private Item drop;

    public LootResultButton() {
        super(0, 0, BUTTON_SIZE, BUTTON_SIZE, CommonComponents.EMPTY);
    }

    public Item getDrop() {
        return drop;
    }

    public void showDrop(Item drop) {
        this.drop = drop;
    }

    @NotNull
    public Tooltip getTooltip() {
        return Tooltip.create(drop.getDefaultInstance().getHoverName());
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, getX(), getY(), width, height);
        ItemStack stack = drop.getDefaultInstance();
        graphics.fakeItem(stack, getX() + 4, getY() + 4);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }
}
