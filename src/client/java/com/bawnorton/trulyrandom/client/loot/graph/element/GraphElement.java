package com.bawnorton.trulyrandom.client.loot.graph.element;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class GraphElement {
    private final Set<GraphElement> from;
    private final Set<GraphElement> to;
    private boolean hovered;

    protected GraphElement() {
        from = new HashSet<>();
        to = new HashSet<>();
    }

    public Set<GraphElement> getFrom() {
        return from;
    }

    public Set<GraphElement> getTo() {
        return to;
    }

    public void addFrom(GraphElement from) {
        this.from.add(from);
        from.to.add(this);
    }

    public void addTo(GraphElement to) {
        this.to.add(to);
        to.from.add(this);
    }

    public abstract void render(DrawContext context, MinecraftClient client, int x, int y, float scale);

    protected abstract Text getTooltip();

    public void drawTooltip(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Text tooltip = getTooltip();
        context.drawTooltip(textRenderer, tooltip, mouseX - textRenderer.getWidth(tooltip) - 15, mouseY);
    }

    public void onHovered() {
        hovered = true;
        getToRecursivly().forEach(element -> element.hovered = true);
    }
    
    public void clearHovered() {
        hovered = false;
        getToRecursivly().forEach(element -> element.hovered = false);
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
            GraphElement fromElement = element.supplyGraph(builder, depth - 1, seen);
            builder.addEdge(fromElement, this);
        }
        for(GraphElement element : to) {
            GraphElement toElement = element.supplyGraph(builder, depth - 1, seen);
            builder.addEdge(this, toElement);
        }
        return this;
    }
}
