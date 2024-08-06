package com.bawnorton.trulyrandom.graph.positioner;

import com.bawnorton.trulyrandom.graph.Graph;

public interface Positioner<T> {
    void position(Graph<T> graph);
}
