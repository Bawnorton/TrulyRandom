package com.bawnorton.trulyrandom.graph;

import org.joml.Vector2d;
import java.util.Objects;

public final class Node<T> {
    private final T value;
    private final Vector2d position;

    public Node(T value, Vector2d position) {
        this.value = value;
        this.position = position;
    }

    public Node(T value) {
        this(value, new Vector2d(0, 0));
    }

    public T value() {
        return value;
    }

    public Vector2d position() {
        return position;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Node<?>) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Node[%s]".formatted(value);
    }

}
