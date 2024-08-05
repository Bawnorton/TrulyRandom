package com.bawnorton.trulyrandom.graph;

import org.joml.Vector2d;

public record Node<T>(T value, Vector2d position) {
    public Node(T value) {
        this(value, new Vector2d(0, 0));
    }
}
