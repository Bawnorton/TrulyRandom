package com.bawnorton.trulyrandom.client.screen.lootbook;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class LootResultButton extends ClickableWidget {
    private static final Identifier SLOT_TEXTURE = Identifier.ofVanilla("recipe_book/slot_craftable");
    public static final int BUTTON_SIZE = 25;
    private Item drop;

    public LootResultButton() {
        super(0, 0, BUTTON_SIZE, BUTTON_SIZE, ScreenTexts.EMPTY);
    }

    public Item getDrop() {
        return drop;
    }

    public void showDrop(Item drop) {
        this.drop = drop;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SLOT_TEXTURE, getX(), getY(), width, height);
        ItemStack stack = drop.getDefaultStack();
        context.drawItemWithoutEntity(stack, getX() + 4, getY() + 4);
    }

    @NotNull
    public Tooltip getTooltip() {
        return Tooltip.of(drop.getName());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
