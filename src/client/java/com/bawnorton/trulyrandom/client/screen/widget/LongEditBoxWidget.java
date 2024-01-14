package com.bawnorton.trulyrandom.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.text.Text;

public class LongEditBoxWidget extends EditBoxWidget {
    public LongEditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, long message) {
        super(textRenderer, x, y, width, height, placeholder, Text.of(Long.toString(message)));
    }

    public long getLong() {
        try {
            return Long.parseLong(getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setLong(long value) {
        setText(Long.toString(value));
    }

    @Override
    public void setText(String text) {
        String original = getText();
        try {
            Long.parseLong(text);
            super.setText(text);
        } catch (NumberFormatException e) {
            super.setText(original);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= '0' && chr <= '9') {
            String original = getText();
            boolean success = super.charTyped(chr, modifiers);
            if (success) {
                try {
                    Long.parseLong(getText());
                } catch (NumberFormatException e) {
                    setText(original);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(active && visible) return super.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(active && visible) return super.isMouseOver(mouseX, mouseY);
        return false;
    }
}
