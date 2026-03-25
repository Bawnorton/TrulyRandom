package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.graph.TrackingGraphBookController;
import com.bawnorton.trulyrandom.client.graph.TrackingGraph;
import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.screen.widget.ItemButton;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.ColoredQuadGuiElementRenderState;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
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

    private TrackingGraphBookController controller;
    private TrackingGraph graph;
    private MinecraftClient client;
    private Item item;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private float scale;

    private ItemButton centre;
    private ItemButton close;

    public void initalize(MinecraftClient client, TrackingGraphBookController controller, LootBookWidget widget, int x, int y) {
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

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, WIDTH, HEIGHT);

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;

        if (graph == null) {
            Text text = Text.translatable("trulyrandom.loot_book.no_graph");
            int width = client.textRenderer.getWidth(text);
            context.drawText(client.textRenderer, text, centreX - width / 2, centreY - client.textRenderer.fontHeight / 2, Colors.WHITE, false);
            return;
        }

        context.enableScissor(x + BORDER_WIDTH, y + BORDER_WIDTH, x + WIDTH - BORDER_WIDTH, y + HEIGHT - BORDER_WIDTH);
        matrices.scale(scale, scale);
        graph.forEachVertex(vertex -> {
            ScreenPos screenPos = mapToScreen(vertex, centreX, centreY, mouseX, mouseY);
            if (screenPos == null) return;

            if (mouseX >= screenPos.x() && mouseX <= screenPos.x() + (32 * scale) && mouseY >= screenPos.y() && mouseY <= screenPos.y() + (32 * scale)) {
                vertex.onHovered();
            }
        });
        graph.forEachEdge((source, target) -> {
            if(source.isHovered()) return;
            
            Vector2f sourcePos = graph.getPos(source);
            Vector2f targetPos = graph.getPos(target);
            int sourceX = centreX + offsetX + Math.round(sourcePos.x);
            int sourceY = centreY + offsetY + Math.round(sourcePos.y);
            int targetX = centreX + offsetX + Math.round(targetPos.x);
            int targetY = centreY + offsetY + Math.round(targetPos.y);
            int halfX = sourceX + (targetX - sourceX) / 2;

            context.fill(sourceX, sourceY + 1, halfX, sourceY - 1, Colors.WHITE);
            context.fill(halfX - 1, sourceY, halfX + 1, targetY, Colors.WHITE);
            context.fill(halfX, targetY + 1, targetX, targetY - 1, Colors.WHITE);
        });
        graph.forEachEdge((source, target) -> {
            if(!source.isHovered()) return;
            
            Vector2f sourcePos = graph.getPos(source);
            Vector2f targetPos = graph.getPos(target);
            int sourceX = centreX + offsetX + Math.round(sourcePos.x);
            int sourceY = centreY + offsetY + Math.round(sourcePos.y);
            int targetX = centreX + offsetX + Math.round(targetPos.x);
            int targetY = centreY + offsetY + Math.round(targetPos.y);
            int halfX = sourceX + (targetX - sourceX) / 2;
            
            context.fill(sourceX, sourceY + 1, halfX, sourceY - 1, Colors.GREEN);
            context.fill(halfX - 1, sourceY, halfX + 1, targetY, Colors.GREEN);
            context.fill(halfX, targetY + 1, targetX, targetY - 1, Colors.GREEN);

            // draw arrow head
            ScreenRect scissorArea = context.scissorStack.peekLast();
            context.state.addSimpleElement(new SimpleGuiElementRenderState() {
                @Override
                public @Nullable ScreenRect bounds() {
                    return scissorArea;
                }

                @Override
                public void setupVertices(VertexConsumer vertices, float depth) {
                    float scaledX = scale * (targetX - 20);
                    float scaledY = scale * targetY;
                    vertices.vertex(matrices, scaledX, scaledY - 6 * scale, depth).color(Colors.GREEN);
                    vertices.vertex(matrices, scaledX, scaledY + 6 * scale, depth).color(Colors.GREEN);
                    vertices.vertex(matrices, scaledX + 6 * scale, scaledY, depth).color(Colors.GREEN);
                    vertices.vertex(matrices, scaledX, scaledY - 6 * scale, depth).color(Colors.GREEN);
                }

                public RenderPipeline pipeline() { return RenderPipelines.GUI; }
                public TextureSetup textureSetup() { return TextureSetup.empty(); }
                public @Nullable ScreenRect scissorArea() { return scissorArea; }
            });
        });
        matrices.pushMatrix();
        graph.forEachVertex(vertex -> {
            Vector2f pos = graph.getPos(vertex);
            int xPos = centreX + offsetX + Math.round(pos.x) - 16;
            int yPos = centreY + offsetY + Math.round(pos.y) - 16;
            vertex.renderBackground(context, xPos, yPos, 32, 32);
            vertex.render(context, client, mouseX, mouseY, xPos + 16, yPos + 16, scale);
        });
        matrices.popMatrix();

        graph.forEachVertex(GraphElement::clearHovered);
        context.disableScissor();

        matrices.popMatrix();
        centre.render(context, mouseX, mouseY, delta);
        close.render(context, mouseX, mouseY, delta);
    }

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        if(item == null) return;

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;
        graph.forEachVertex(vertex -> {
            ScreenPos screenPos = mapToScreen(vertex, centreX, centreY, mouseX, mouseY);
            if (screenPos == null) return;

            if (mouseX >= screenPos.x() && mouseX <= screenPos.x() + (32 * scale) && mouseY >= screenPos.y() && mouseY <= screenPos.y() + (32 * scale)) {
                vertex.drawTooltip(context, mouseX, mouseY);
            }
        });
    }


    private @Nullable LootBookGraph.ScreenPos mapToScreen(GraphElement vertex, int centreX, int centreY, int mouseX, int mouseY) {
        Vector2f pos = graph.getPos(vertex);
        int x = (int) ((centreX + offsetX + Math.round(pos.x) - 16) * scale);
        int y = (int) ((centreY + offsetY + Math.round(pos.y) - 16) * scale);
        if (!inBounds(mouseX, mouseY)) return null;

        return new ScreenPos(x, y);
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
        return isOpen() && inBounds(mouseX, mouseY);
    }

    public void show(Item item) {
        this.item = item;
        controller.setGraphItem(item);
        if(item == null) return;

        try {
            graph = controller.createGraph(item);
        } catch (Exception e) {
            TrulyRandom.LOGGER.error("Couldn't create graph", e);
        }
        if (controller.isNewGraphItem()) {
            setScale(1);
            moveToRoot();
        } else {
            setScale(controller.scale);
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
            setScale(1);
            moveTo((int) -newPos.x, (int) -newPos.y);
        });
        Vector2f rootPos = graph.getRootPos();
        setScale(1);
        moveTo((int) -rootPos.x, (int) -rootPos.y);
    }

    public boolean scale(float amount) {
        setScale(scale * amount);
        float minScale = 0.2f;
        float maxScale = 4;
        if (this.scale < minScale || this.scale > maxScale) {
            setScale(MathHelper.clamp(this.scale, minScale, maxScale));
            return false;
        }
        return true;
    }

    private void setScale(float scale) {
        this.scale = scale;
        controller.scale = this.scale;
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

    private record ScreenPos(int x, int y) {
    }
}
