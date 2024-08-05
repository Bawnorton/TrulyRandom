package com.bawnorton.trulyrandom.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph<T> {
    private final Set<Node<T>> nodes;
    private final Set<Edge<T>> edges;

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public boolean addNode(Node<T> node) {
        return nodes.add(node);
    }

    public void addEdge(Node<T> origin, Node<T> destination) {
        edges.add(new Edge<>(origin, destination));
    }

    public List<Node<T>> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge<T>> getEdges() {
        return new ArrayList<>(edges);
    }
}
