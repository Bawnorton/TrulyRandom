package com.bawnorton.trulyrandom.client.screen.widget;


import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.Item;

public class ItemButton extends Button {
    private final Item item;
    private final WidgetSprites background;

    protected ItemButton(Item item, WidgetSprites background, int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, item.getDefaultInstance().getItemName(), onPress, Button.DEFAULT_NARRATION);
        this.item = item;
        this.background = background;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, background.get(true, isHovered()), getX(), getY(), getWidth(), getHeight());
        graphics.fakeItem(item.getDefaultInstance(), getX() + (getWidth() - 16) / 2, getY() + (getHeight() - 16) / 2);
    }

    public static ItemButton.Builder builder(Item item, OnPress onPress) {
        return new Builder(item, onPress);
    }

    public static class Builder {
        private Item item;
        private OnPress onPress;
        private WidgetSprites background;
        private int x;
        private int y;
        private int width;
        private int height;

        private Builder(Item item, OnPress onPress) {
            this.item = item;
            this.onPress = onPress;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder onPress(OnPress onPress) {
            this.onPress = onPress;
            return this;
        }

        public Builder background(WidgetSprites background) {
            this.background = background;
            return this;
        }

        public Builder position(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        public Builder dimensions(int width, int height) {
            if(width < 16 || height < 16) {
                throw new IllegalArgumentException("Dimensions must be at least 16x16");
            }
            this.width = width;
            this.height = height;
            return this;
        }

        public ItemButton build() {
            return new ItemButton(item, background, x, y, width, height, onPress);
        }
    }
}
