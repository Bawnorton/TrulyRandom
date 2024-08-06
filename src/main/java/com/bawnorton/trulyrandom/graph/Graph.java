package com.bawnorton.trulyrandom.graph;

import com.bawnorton.trulyrandom.graph.positioner.Positioner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph<T> {
    private final Set<Node<T>> nodes;
    private final Set<Edge<T>> edges;
    private Node<T> root;

    public Graph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public void setRoot(Node<T> root) {
        this.root = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    public boolean addNode(Node<T> node) {
        return nodes.add(node);
    }

    public void addEdge(Node<T> origin, Node<T> destination) {
        edges.add(new Edge<>(origin, destination));
    }

    public List<Edge<T>> getIncomingEdges(Node<T> node) {
        List<Edge<T>> incomingEdges = new ArrayList<>();
        for (Edge<T> edge : edges) {
            if(edge.target().equals(node)) {
                incomingEdges.add(edge);
            }
        }
        return incomingEdges;
    }

    public List<Edge<T>> getOutgoingEdges(Node<T> node) {
        List<Edge<T>> outgoingEdges = new ArrayList<>();
        for (Edge<T> edge : edges) {
            if(edge.source().equals(node)) {
                outgoingEdges.add(edge);
            }
        }
        return outgoingEdges;
    }

    public List<Node<T>> getNodes() {
        return new ArrayList<>(nodes);
    }

    public List<Edge<T>> getEdges() {
        return new ArrayList<>(edges);
    }

    public void position(Positioner<T> positioner) {
        positioner.position(this);
    }
}
