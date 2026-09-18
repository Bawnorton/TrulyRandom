package com.bawnorton.trulyrandom.client.screen.lootbook;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.graph.TrackingGraph;
import com.bawnorton.trulyrandom.client.graph.TrackingGraphBookController;
import com.bawnorton.trulyrandom.client.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.screen.widget.ItemButton;
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

import java.util.function.BiConsumer;

//~ if <=26.1.2 'renderpearl.api' -> 'blaze3d'
import com.mojang.renderpearl.api.pipeline.RenderPipeline;

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
        BiConsumer<Boolean, Integer> renderEdges = (isHoveredPass, color) -> {
            graph.forEachEdge((source, target) -> {
                if (source.isHovered() != isHoveredPass) return;

                Vector2f sourcePos = graph.getPos(source);
                Vector2f targetPos = graph.getPos(target);
                float sourceX = centreX + offsetX + sourcePos.x;
                float sourceY = centreY + offsetY + sourcePos.y;
                float targetX = centreX + offsetX + targetPos.x;
                float targetY = centreY + offsetY + targetPos.y;
                float halfX = sourceX + (targetX - sourceX) / 2.0f;

                ScreenRectangle scissorArea = graphics.scissorStack.peek();
                graphics.guiRenderState.addGuiElement(new GuiElementRenderState() {
                    @Override
                    public @Nullable ScreenRectangle bounds() {
                        return scissorArea;
                    }

                    @Override
                    public void buildVertices(VertexConsumer vertexConsumer) {
                        int segments = 50;
                        float thickness = 2.0f;

                        float prevX = sourceX;
                        float prevY = sourceY;

                        float arrowTipX = targetX;
                        float arrowTipY = targetY;
                        float arrowDx = targetX - sourceX;
                        float arrowDy = targetY - sourceY;

                        for (int i = 1; i <= segments; i++) {
                            float t = (float) i / segments;
                            float u = 1.0f - t;

                            float currX = (u*u*u * sourceX) + (3*u*u*t * halfX) + (3*u*t*t * halfX) + (t*t*t * targetX);
                            float currY = (u*u*u * sourceY) + (3*u*u*t * sourceY) + (3*u*t*t * targetY) + (t*t*t * targetY);

                            if (Math.abs(currX - targetX) <= 16 && Math.abs(currY - targetY) <= 16) {
                                arrowTipX = currX;
                                arrowTipY = currY;
                                arrowDx = currX - prevX;
                                arrowDy = currY - prevY;
                                break;
                            }

                            float dx = currX - prevX;
                            float dy = currY - prevY;
                            float len = (float) Math.sqrt(dx * dx + dy * dy);

                            if (len > 0) {
                                float nx = (-dy / len) * (thickness / 2.0f);
                                float ny = (dx / len) * (thickness / 2.0f);

                                float sx1 = prevX * scale, sy1 = prevY * scale;
                                float sx2 = currX * scale, sy2 = currY * scale;
                                float snx = nx * scale, sny = ny * scale;

                                vertexConsumer.addVertexWith2DPose(matrices, sx1 - snx, sy1 - sny).setColor(color);
                                vertexConsumer.addVertexWith2DPose(matrices, sx1 + snx, sy1 + sny).setColor(color);
                                vertexConsumer.addVertexWith2DPose(matrices, sx2 + snx, sy2 + sny).setColor(color);
                                vertexConsumer.addVertexWith2DPose(matrices, sx2 - snx, sy2 - sny).setColor(color);
                            }
                            prevX = currX;
                            prevY = currY;
                        }

                        float dirLen = (float) Math.sqrt(arrowDx * arrowDx + arrowDy * arrowDy);
                        if (dirLen > 0) {
                            arrowDx /= dirLen;
                            arrowDy /= dirLen;
                        }

                        int arrowLength = 6;
                        int arrowHalfHeight = 6;

                        float bx = arrowTipX - arrowDx * arrowLength;
                        float by = arrowTipY - arrowDy * arrowLength;

                        float nx = -arrowDy * arrowHalfHeight;
                        float ny = arrowDx * arrowHalfHeight;

                        float pLeftX = bx + nx;
                        float pLeftY = by + ny;
                        float pRightX = bx - nx;
                        float pRightY = by - ny;

                        vertexConsumer.addVertexWith2DPose(matrices, pRightX * scale, pRightY * scale).setColor(color);
                        vertexConsumer.addVertexWith2DPose(matrices, pLeftX * scale, pLeftY * scale).setColor(color);
                        vertexConsumer.addVertexWith2DPose(matrices, arrowTipX * scale, arrowTipY * scale).setColor(color);
                        vertexConsumer.addVertexWith2DPose(matrices, pRightX * scale, pRightY * scale).setColor(color);
                    }

                    public RenderPipeline pipeline() { return RenderPipelines.GUI; }
                    public TextureSetup textureSetup() { return TextureSetup.noTexture(); }
                    public @Nullable ScreenRectangle scissorArea() { return scissorArea; }
                });
            });
        };

        renderEdges.accept(false, CommonColors.WHITE);
        renderEdges.accept(true, CommonColors.GREEN);

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
        if(item == null || graph == null) return;

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

        if(inBounds(event.x(), event.y())) {
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


    public void refresh() {
        if(item == null) return;

        try {
            graph = controller.createGraph(item);
        } catch (Exception e) {
            TrulyRandom.LOGGER.error("Couldn't create graph", e);
        }
    }

    public void moveTo(int x, int y) {
        offsetX = x;
        offsetY = y;
        controller.offsetX = offsetX;
        controller.offsetY = offsetY;
    }

    public void moveToRoot() {
        if (graph == null) return;

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
