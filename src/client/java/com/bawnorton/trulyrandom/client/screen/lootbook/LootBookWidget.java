package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class LootBookWidget implements Drawable, Element, Selectable {
    public static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
            TrulyRandom.id("loot_book/button"),
            TrulyRandom.id("loot_book/button_focused")
    );
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of("minecraft", "textures/gui/recipe_book.png");

    private int rightOffset;
    private int parentWidth;
    private int parentHeight;

    private MinecraftClient client;
    private TextFieldWidget searchField;

    private boolean searching;
    private boolean open;
    private boolean narrow;

    public void initialize(int parentWidth, int parentHeight, MinecraftClient client, boolean narrow, PlayerScreenHandler handler) {
        this.client = client;
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.narrow = narrow;
        if(this.open) {
            this.reset();
        }
    }

    public void reset() {
        this.rightOffset = narrow ? 0 : 86;
        int x = (this.parentWidth - 147) / 2 + this.rightOffset;
        int y = (this.parentHeight - 166) / 2;
        String search = this.searchField == null ? "" : this.searchField.getText();
        this.searchField = new TextFieldWidget(this.client.textRenderer, x + 25, y + 13, 81, client.textRenderer.fontHeight + 5, Text.translatable("itemGroup.trulyrandom.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setVisible(true);
        this.searchField.setEditableColor(16777215);
        this.searchField.setText(search);
        this.searchField.setPlaceholder(Text.translatable("gui.recipebook.search_hint").formatted(Formatting.ITALIC).formatted(Formatting.GRAY));
    }

    public int findLeftEdge(int width, int backgroundWidth) {
        int edge = (width - backgroundWidth) / 2;
        if(this.isOpen() && !narrow) {
            edge -= 77;
        }
        return edge;
    }

    public void toggleOpen() {
        this.setOpen(!this.isOpen());
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean opened) {
        if(opened) {
            this.reset();
        }
        this.open = opened;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(!open) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100F);
        int x = (this.parentWidth - 147) / 2 + this.rightOffset;
        int y = (this.parentHeight - 166) / 2;
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 1, 1, 147, 166);
        this.searchField.render(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return this.open ? SelectionType.HOVERED : SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
