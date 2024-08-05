package com.bawnorton.trulyrandom.client.loot.graph;

import com.bawnorton.trulyrandom.graph.Graph;
import com.bawnorton.trulyrandom.graph.Node;
import net.minecraft.client.gui.DrawContext;
import java.util.HashSet;
import java.util.Set;

public abstract class GraphElement {
    private final Set<GraphElement> from;
    private final Set<GraphElement> to;

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

    public abstract void render(DrawContext drawContext, int x, int y);

    public Node<GraphElement> supplyGraph(Graph<GraphElement> graph) {
        Node<GraphElement> node = new Node<>(this);
        if(graph.addNode(node)) {
            for(GraphElement from : from) {
                Node<GraphElement> fromElement = from.supplyGraph(graph);
                graph.addEdge(fromElement, node);
            }
            for(GraphElement to : to) {
                Node<GraphElement> toElement = to.supplyGraph(graph);
                graph.addEdge(node, toElement);
            }
        }
        return node;
    }
}
