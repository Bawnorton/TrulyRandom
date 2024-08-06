package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.loot.LootBookController;
import com.bawnorton.trulyrandom.client.loot.graph.GraphElement;
import com.bawnorton.trulyrandom.graph.Graph;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.joml.Vector2d;

public class LootBookGraph implements Drawable, Element {
    private static final Identifier BACKGROUND_TEXTURE = TrulyRandom.id("loot_book/graph");
    private static final int BORDER_WIDTH = 8;

    public static final int HEIGHT = 167;
    public static final int WIDTH = 325;

    private LootBookController controller;
    private Graph<GraphElement> graph;
    private MinecraftClient client;
    private Item item;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;

    public void initalize(MinecraftClient client, LootBookController controller, int x, int y) {
        this.client = client;
        this.controller = controller;
        this.x = x;
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(item == null) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);
        context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, WIDTH, HEIGHT);

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;

        context.enableScissor(x + BORDER_WIDTH, y + BORDER_WIDTH, x + WIDTH - BORDER_WIDTH, y + HEIGHT - BORDER_WIDTH);
        graph.getNodes().forEach(node -> {
            Vector2d pos = node.position();
            node.value().render(context, centreX + (int) pos.x + offsetX, centreY + (int) pos.y + offsetY);
        });
        context.disableScissor();

        context.getMatrices().pop();
    }

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
    }

    private boolean inBounds(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x + WIDTH && mouseY > y && mouseY < y + HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(item == null) return false;

        return inBounds(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(item == null) return false;

        if(button == 0 && inBounds(mouseX, mouseY)) {
            offsetX += (int) deltaX;
            offsetY += (int) deltaY;
            return true;
        }
        return false;
    }

    public void show(Item item) {
        this.item = item;
        if(item == null) return;

        this.graph = controller.createGraph(item);
        offsetX = 0;
        offsetY = 0;
    }

    public void hide() {
        show(null);
    }

    @Override
    public void setFocused(boolean focused) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public boolean isOpen() {
        return item != null;
    }
}
