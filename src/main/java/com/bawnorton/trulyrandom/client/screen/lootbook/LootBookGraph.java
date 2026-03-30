package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.graph.TrackingGraphBookController;
import com.bawnorton.trulyrandom.client.graph.TrackingGraph;
import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.screen.widget.ItemButton;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

public class LootBookGraph implements Renderable, GuiEventListener {
    private static final Identifier BACKGROUND_TEXTURE = TrulyRandom.id("loot_book/graph");
    private static final WidgetSprites BUTTON_BACKGROUNDS = new WidgetSprites(
            AdvancementWidgetType.UNOBTAINED.frameSprite(AdvancementType.CHALLENGE),
            AdvancementWidgetType.OBTAINED.frameSprite(AdvancementType.CHALLENGE)
    );
    private static final int BORDER_WIDTH = 8;

    public static final int HEIGHT = 167;
    public static final int WIDTH = 325;

    private TrackingGraphBookController controller;
    private TrackingGraph graph;
    private Minecraft minecraft;
    private Item item;
    private int x;
    private int y;
    private int offsetX;
    private int offsetY;
    private float scale;

    private ItemButton centre;
    private ItemButton close;

    public void initalize(Minecraft minecraft, TrackingGraphBookController controller, LootBookWidget widget, int x, int y) {
        this.minecraft = minecraft;
        this.controller = controller;
        this.x = x;
        this.y = y;

        centre = ItemButton.builder(Items.COMPASS, _ -> moveToRoot())
                .position(x + WIDTH - 38, y + HEIGHT - 38)
                .dimensions(24, 24)
                .background(BUTTON_BACKGROUNDS)
                .build();
        centre.setTooltip(Tooltip.create(Component.translatable("trulyrandom.loot_book.center")));

        close = ItemButton.builder(Items.BARRIER, _ -> widget.closeGraph())
                .position(x + WIDTH - 38, y + 13)
                .dimensions(24, 24)
                .background(BUTTON_BACKGROUNDS)
                .build();
        close.setTooltip(Tooltip.create(Component.translatable("trulyrandom.loot_book.close")));
    }

    public void setY(int y) {
        this.y = y;
        centre.setY(y + HEIGHT - 38);
        close.setY(y + 13);
    }

    public int getY() {
        return y;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if(item == null) return;

        Matrix3x2fStack matrices = graphics.pose();
        matrices.pushMatrix();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, x, y, WIDTH, HEIGHT);

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;

        if (graph == null) {
            Component component = Component.translatable("trulyrandom.loot_book.no_graph");
            int width = minecraft.font.width(component);
            graphics.text(minecraft.font, component, centreX - width / 2, centreY - minecraft.font.lineHeight / 2, CommonColors.WHITE, false);
            return;
        }

        graphics.enableScissor(x + BORDER_WIDTH, y + BORDER_WIDTH, x + WIDTH - BORDER_WIDTH, y + HEIGHT - BORDER_WIDTH);
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

            graphics.fill(sourceX, sourceY + 1, halfX, sourceY - 1, CommonColors.WHITE);
            graphics.fill(halfX - 1, sourceY, halfX + 1, targetY, CommonColors.WHITE);
            graphics.fill(halfX, targetY + 1, targetX, targetY - 1, CommonColors.WHITE);
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

            graphics.fill(sourceX, sourceY + 1, halfX, sourceY - 1, CommonColors.GREEN);
            graphics.fill(halfX - 1, sourceY, halfX + 1, targetY, CommonColors.GREEN);
            graphics.fill(halfX, targetY + 1, targetX, targetY - 1, CommonColors.GREEN);

            // draw arrow head
            ScreenRectangle scissorArea = graphics.scissorStack.peek();
            graphics.guiRenderState.addGuiElement(new GuiElementRenderState() {
                @Override
                public @Nullable ScreenRectangle bounds() {
                    return scissorArea;
                }

                @Override
                public void buildVertices(VertexConsumer vertexConsumer) {
                    float scaledX = scale * (targetX - 20);
                    float scaledY = scale * targetY;
                    vertexConsumer.addVertexWith2DPose(matrices, scaledX, scaledY - 6 * scale).setColor(CommonColors.GREEN);
                    vertexConsumer.addVertexWith2DPose(matrices, scaledX, scaledY + 6 * scale).setColor(CommonColors.GREEN);
                    vertexConsumer.addVertexWith2DPose(matrices, scaledX + 6 * scale, scaledY).setColor(CommonColors.GREEN);
                    vertexConsumer.addVertexWith2DPose(matrices, scaledX, scaledY - 6 * scale).setColor(CommonColors.GREEN);
                }

                public RenderPipeline pipeline() { return RenderPipelines.GUI; }
                public TextureSetup textureSetup() { return TextureSetup.noTexture(); }
                public @Nullable ScreenRectangle scissorArea() { return scissorArea; }
            });
        });
        matrices.pushMatrix();
        graph.forEachVertex(vertex -> {
            Vector2f pos = graph.getPos(vertex);
            int xPos = centreX + offsetX + Math.round(pos.x) - 16;
            int yPos = centreY + offsetY + Math.round(pos.y) - 16;
            vertex.extractBackground(graphics, xPos, yPos, 32, 32);
            vertex.extractRenderState(graphics, minecraft, mouseX, mouseY, xPos + 16, yPos + 16, scale);
        });
        matrices.popMatrix();

        graph.forEachVertex(GraphElement::clearHovered);
        graphics.disableScissor();

        matrices.popMatrix();
        centre.extractRenderState(graphics, mouseX, mouseY, a);
        close.extractRenderState(graphics, mouseX, mouseY, a);
    }

    public void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if(item == null) return;

        int centreX = x + WIDTH / 2;
        int centreY = y + HEIGHT / 2;
        graph.forEachVertex(vertex -> {
            ScreenPos screenPos = mapToScreen(vertex, centreX, centreY, mouseX, mouseY);
            if (screenPos == null) return;

            if (mouseX >= screenPos.x() && mouseX <= screenPos.x() + (32 * scale) && mouseY >= screenPos.y() && mouseY <= screenPos.y() + (32 * scale)) {
                try {
                    vertex.extractTooltip(graphics, mouseX, mouseY);
                } catch (RuntimeException e) {
                    graphics.setTooltipForNextFrame(Component.literal("Error loading tooltip"), mouseX, mouseY);
                }
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
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(item == null) return false;

        if(centre.mouseClicked(event, doubleClick)) {
            return true;
        }

        if(close.mouseClicked(event, doubleClick)) {
            return true;
        }

        return inBounds(event.x(), event.y());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if(item == null) return false;

        if(event.button() == 0 && inBounds(event.x(), event.y())) {
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
            setScale(Mth.clamp(this.scale, minScale, maxScale));
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
