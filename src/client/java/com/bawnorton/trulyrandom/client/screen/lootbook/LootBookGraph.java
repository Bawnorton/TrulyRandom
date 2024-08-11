package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.loot.LootBookController;
import com.bawnorton.trulyrandom.client.loot.graph.LootGraph;
import com.bawnorton.trulyrandom.client.loot.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.screen.widget.ItemButton;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;

public class LootBookGraph implements Drawable, Element {
    private static final Identifier BACKGROUND_TEXTURE = TrulyRandom.id("loot_book/graph");
    private static final ButtonTextures BUTTON_BACKGROUNDS = new ButtonTextures(
            AdvancementObtainedStatus.UNOBTAINED.getFrameTexture(AdvancementFrame.CHALLENGE),
            AdvancementObtainedStatus.OBTAINED.getFrameTexture(AdvancementFrame.CHALLENGE)
    );
    private static final int BORDER_WIDTH = 8;

    public static final int HEIGHT = 167;
    public static final int WIDTH = 325;

    private LootBookController controller;
    private LootGraph graph;
    private MinecraftClient client;
    private Item item;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private float scale;

    private ItemButton centre;
    private ItemButton close;

    public void initalize(MinecraftClient client, LootBookController controller, LootBookWidget widget, int x, int y) {
        this.client = client;
        this.controller = controller;
        this.x = x;
        this.y = y;

        centre = ItemButton.builder(Items.COMPASS, button -> moveToRoot())
                .position(x + WIDTH - 38, y + HEIGHT - 38)
                .dimensions(24, 24)
                .background(BUTTON_BACKGROUNDS)
                .build();
        centre.setTooltip(Tooltip.of(Text.translatable("trulyrandom.loot_book.center")));

        close = ItemButton.builder(Items.BARRIER, button -> widget.closeGraph())
                .position(x + WIDTH - 38, y + 13)
                .dimensions(24, 24)
                .background(BUTTON_BACKGROUNDS)
                .build();
        close.setTooltip(Tooltip.of(Text.translatable("trulyrandom.loot_book.close")));
    }

    public void setY(int y) {
        this.y = y;
        centre.setY(y + HEIGHT - 38);
        close.setY(y + 13);
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

        if (graph == null) {
            Text text = Text.translatable("trulyrandom.loot_book.no_graph");
            int width = client.textRenderer.getWidth(text);
            context.drawText(client.textRenderer, text, centreX - width / 2, centreY - client.textRenderer.fontHeight / 2, Colors.WHITE, false);
            return;
        }

        context.getMatrices().scale(scale, scale, 1);
        context.enableScissor(x + BORDER_WIDTH, y + BORDER_WIDTH, x + WIDTH - BORDER_WIDTH, y + HEIGHT - BORDER_WIDTH);
        graph.forEachVertex(vertex -> {
            Vector2f pos = graph.getPos(vertex);
            int xPos = (int) ((centreX + offsetX + Math.round(pos.x) - 16) * scale);
            int yPos = (int) ((centreY + offsetY + Math.round(pos.y) - 16) * scale);
            if (mouseX >= xPos && mouseX <= xPos + (32 * scale) && mouseY >= yPos && mouseY <= yPos + (32 * scale)) {
                vertex.onHovered();
            }
        });
        graph.forEachEdge((source, target) -> {
            Vector2f sourcePos = graph.getPos(source);
            Vector2f targetPos = graph.getPos(target);
            int sourceX = centreX + offsetX + Math.round(sourcePos.x);
            int sourceY = centreY + offsetY + Math.round(sourcePos.y);
            int targetX = centreX + offsetX + Math.round(targetPos.x);
            int targetY = centreY + offsetY + Math.round(targetPos.y);
            int halfX = sourceX + (targetX - sourceX) / 2;

            int colour;
            context.getMatrices().push();
            if (source.isHovered()) {
                colour = Colors.GREEN;
                context.getMatrices().translate(0, 0, 10);
            } else {
                colour = Colors.WHITE;
            }

            context.fill(sourceX, sourceY + 1, halfX, sourceY - 1, colour);
            context.fill(halfX - 1, sourceY, halfX + 1, targetY, colour);
            context.fill(halfX, targetY + 1, targetX, targetY - 1, colour);
            context.getMatrices().pop();
        });
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);
        graph.forEachVertex(vertex -> {
            Vector2f pos = graph.getPos(vertex);
            int xPos = centreX + offsetX + Math.round(pos.x) - 16;
            int yPos = centreY + offsetY + Math.round(pos.y) - 16;

            Identifier texture;
            if (vertex.getTo().isEmpty()) {
                texture = AdvancementObtainedStatus.OBTAINED.getFrameTexture(AdvancementFrame.CHALLENGE);
            } else {
                if (vertex.isHovered()) {
                    RenderSystem.setShaderColor(0, 1, 0, 1);
                }
                texture = AdvancementObtainedStatus.UNOBTAINED.getFrameTexture(AdvancementFrame.TASK);
            }
            context.drawGuiTexture(texture, xPos, yPos, 32, 32);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            vertex.render(context, client, xPos + 16, yPos + 16, scale);
        });
        context.getMatrices().pop();

        graph.forEachVertex(GraphElement::clearHovered);
        context.disableScissor();


        context.getMatrices().pop();

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 500);
        centre.render(context, mouseX, mouseY, delta);
        close.render(context, mouseX, mouseY, delta);
        context.getMatrices().pop();
    }

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        if(item == null) return;

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1000);
        graph.forEachVertex(vertex -> {
            Vector2f pos = graph.getPos(vertex);
            int xPos = (int) ((centreX + offsetX + Math.round(pos.x) - 16) * scale);
            int yPos = (int) ((centreY + offsetY + Math.round(pos.y) - 16) * scale);
            if (mouseX >= xPos && mouseX <= xPos + (32 * scale) && mouseY >= yPos && mouseY <= yPos + (32 * scale)) {
                vertex.drawTooltip(context, mouseX, mouseY);
            }
        });
        context.getMatrices().pop();
    }

    private boolean inBounds(double mouseX, double mouseY) {
        return mouseX > x && mouseX < x + WIDTH && mouseY > y && mouseY < y + HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(item == null) return false;

        if(centre.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if(close.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return inBounds(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(item == null) return false;

        if(button == 0 && inBounds(mouseX, mouseY)) {
            moveTo(offsetX + (int) (deltaX / scale), offsetY + (int) (deltaY / scale));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (inBounds(mouseX, mouseY)) {
            float scaleFactor = (float) Math.pow(1.1f, verticalAmount);
            double oldScale = this.scale;

            if (scale(scaleFactor)) {
                double newScale = this.scale;

                double scaleDiff = newScale / oldScale;

                double deltaX = mouseX - WIDTH / 2f;
                double deltaY = mouseY - HEIGHT / 2f;

                moveTo(
                        (int) (offsetX - deltaX * (scaleDiff - 1)),
                        (int) (offsetY - deltaY * (scaleDiff - 1))
                );
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return inBounds(mouseX, mouseY);
    }

    public void show(Item item) {
        this.item = item;
        controller.setGraphItem(item);
        if(item == null) return;

        if (controller.isNewGraphItem()) {
            scale = 1;
            try {
                graph = controller.createGraph(item);
                moveToRoot();
            } catch (Exception e) {
                TrulyRandom.LOGGER.error("Couldn't create graph", e);
            }
        } else {
            try {
                graph = controller.createGraph(item);
            } catch (Exception e) {
                TrulyRandom.LOGGER.error("Couldn't create graph", e);
            }
            scale = controller.scale;
            moveTo(controller.offsetX, controller.offsetY);
        }
    }

    public void moveTo(int x, int y) {
        offsetX = x;
        offsetY = y;
        controller.offsetX = offsetX;
        controller.offsetY = offsetY;
    }

    public void moveToRoot() {
        if (graph == null) {
            return;
        }

        graph.addListener(g -> {
            Vector2f newPos = g.getRootPos();
            scale = 1;
            moveTo((int) -newPos.x, (int) -newPos.y);
        });
        Vector2f rootPos = graph.getRootPos();
        scale = 1;
        moveTo((int) -rootPos.x, (int) -rootPos.y);
    }

    public boolean scale(float amount) {
        this.scale *= amount;
        if (this.scale < 0.2 || this.scale > 2) {
            this.scale = Math.min(Math.max(this.scale, 0.2f), 2f);
            controller.scale = this.scale;
            return false;
        }
        controller.scale = this.scale;
        return true;
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
