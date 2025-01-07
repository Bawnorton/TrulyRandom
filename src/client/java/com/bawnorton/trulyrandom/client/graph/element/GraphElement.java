package com.bawnorton.trulyrandom.client.graph.element;

import com.bawnorton.trulyrandom.tracker.loot.drop.TrackingConnection;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.advancement.AdvancementObtainedStatus;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class GraphElement {
    private final Set<GraphElement> from;
    private final Set<GraphElement> to;
    private final Map<GraphElement, TrackingConnection> connections;
    private boolean hovered;

    protected GraphElement() {
        from = new HashSet<>();
        to = new HashSet<>();
        connections = new HashMap<>();
    }

    public Set<GraphElement> getFrom() {
        return from;
    }

    public Set<GraphElement> getTo() {
        return to;
    }

    public void addFrom(GraphElement from, TrackingConnection connection) {
        this.from.add(from);
        from.to.add(this);
        from.connections.put(this, connection);
    }

    public void addTo(GraphElement to, TrackingConnection connection) {
        this.to.add(to);
        this.connections.put(to, connection);
        to.from.add(this);
    }

    protected TrackingConnection getConnection(GraphElement element) {
        return connections.getOrDefault(element, TrackingConnection.NONE);
    }

    public void renderBackground(DrawContext context, int x, int y, int width, int height) {
        Identifier texture;
        int color = -1;
        if (getTo().isEmpty()) {
            texture = AdvancementObtainedStatus.OBTAINED.getFrameTexture(AdvancementFrame.CHALLENGE);
        } else {
            if (isHovered()) {
                color = Colors.GREEN;
            }
            texture = AdvancementObtainedStatus.UNOBTAINED.getFrameTexture(AdvancementFrame.TASK);
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, texture, x, y, width, height, color);
    }

    public abstract void render(DrawContext context, MinecraftClient client, int mouseX, int mouseY, int x, int y, float scale);

    protected abstract Text getTooltip();

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Text tooltip = getTooltip();
        context.drawTooltip(textRenderer, tooltip, mouseX - textRenderer.getWidth(tooltip) - 15, mouseY);
    }

    public void onHovered() {
        onToldToHover();
        getToRecursivly().forEach(GraphElement::onToldToHover);
    }

    public void clearHovered() {
        onToldToClearHover();
        getToRecursivly().forEach(GraphElement::onToldToClearHover);
    }

    protected void onToldToHover() {
        this.hovered = true;
    }

    protected void onToldToClearHover() {
        this.hovered = false;
    }

    public boolean isHovered() {
        return hovered;
    }

    private Set<GraphElement> getToRecursivly() {
        return getToRecursivly(new HashSet<>());
    }

    private Set<GraphElement> getToRecursivly(Set<GraphElement> seen) {
        if(!seen.add(this)) return Collections.emptySet();

        for (GraphElement toElement : to) {
            seen.addAll(toElement.getToRecursivly(seen));
        }
        return seen;
    }
    
    public GraphElement supplyGraph(GraphBuilder<GraphElement, DefaultEdge, ? extends SimpleDirectedGraph<GraphElement, DefaultEdge>> builder) {
        return supplyGraph(builder, 15, new HashSet<>());
    }

    private GraphElement supplyGraph(GraphBuilder<GraphElement, DefaultEdge, ? extends SimpleDirectedGraph<GraphElement, DefaultEdge>> builder, int depth, Set<GraphElement> seen) {
        builder.addVertex(this);
        if(!seen.add(this)) return this;
        if(depth == 0) return this;

        for(GraphElement element : from) {
            if(element.equals(this)) {
                continue;
            }

            GraphElement fromElement = element.supplyGraph(builder, depth - 1, seen);
            builder.addEdge(fromElement, this);
        }
        for(GraphElement element : to) {
            if(element.equals(this)) {
                connections.put(this, TrackingConnection.SELF);
                continue;
            }

            GraphElement toElement = element.supplyGraph(builder, depth - 1, seen);
            builder.addEdge(this, toElement);
        }
        return this;
    }
}
