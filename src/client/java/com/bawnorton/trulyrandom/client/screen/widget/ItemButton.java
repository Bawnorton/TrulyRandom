package com.bawnorton.trulyrandom.client.screen.widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;

public class ItemButton extends ButtonWidget {
    private final Item item;
    private final ButtonTextures background;

    protected ItemButton(Item item, ButtonTextures background, int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, item.getName(), onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.item = item;
        this.background = background;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawItemWithoutEntity(item.getDefaultStack(), getX() + (getWidth() - 16) / 2, getY() + (getHeight() - 16) / 2);
        context.drawGuiTexture(background.get(true, isHovered()), getX(), getY(), getWidth(), getHeight());
    }

    public static ItemButton.Builder builder(Item item, PressAction onPress) {
        return new Builder(item, onPress);
    }

    public static class Builder {
        private Item item;
        private PressAction onPress;
        private ButtonTextures background;
        private int x;
        private int y;
        private int width;
        private int height;

        private Builder(Item item, PressAction onPress) {
            this.item = item;
            this.onPress = onPress;
        }

        public Builder item(Item item) {
            this.item = item;
            return this;
        }

        public Builder onPress(PressAction onPress) {
            this.onPress = onPress;
            return this;
        }

        public Builder background(ButtonTextures background) {
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
