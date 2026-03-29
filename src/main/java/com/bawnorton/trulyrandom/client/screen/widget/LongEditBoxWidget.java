package com.bawnorton.trulyrandom.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class LongEditBoxWidget extends MultiLineEditBox {
    public LongEditBoxWidget(int x, int y, int width, int height, Component placeholder, long message, Font font) {
        super(font, x, y, width, height, placeholder, Component.literal(Long.toString(message)), 0xffe0e0e0, true, 0xffd0d0d0, true, true);
    }

    public long getLong() {
        try {
            return Long.parseLong(getValue());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setLong(long value) {
        setValue(Long.toString(value));
    }

    @Override
    public void setValue(String text) {
        String original = getValue();
        try {
            Long.parseLong(text);
            super.setValue(text);
        } catch (NumberFormatException e) {
            super.setValue(original);
        }
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        int chr = characterEvent.codepoint();
        if (chr >= '0' && chr <= '9' || chr == '-') {
            String original = getValue();
            boolean success = super.charTyped(characterEvent);
            if (success) {
                try {
                    Long.parseLong(getValue());
                } catch (NumberFormatException e) {
                    setValue(original);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(active && visible) return super.mouseClicked(event, doubleClick);
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if(active && visible) return super.isMouseOver(mouseX, mouseY);
        return false;
    }
}
