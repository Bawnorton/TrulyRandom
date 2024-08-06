package com.bawnorton.trulyrandom.graph.positioner;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.graph.Edge;
import com.bawnorton.trulyrandom.graph.Graph;
import com.bawnorton.trulyrandom.graph.Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class OrthogonalTreePositioner<T> implements Positioner<T> {
    private int[][] table;

    @Override
    public void position(Graph<T> graph) {
        Node<T> root = graph.getRoot();
        int depth = determineDepth(graph, root);
        int height = determineHeight(graph, root);
        TrulyRandom.LOGGER.info("Depth: {}", depth);
        TrulyRandom.LOGGER.info("Height: {}", height);
    }

    private int determineHeight(Graph<T> graph, Node<T> root) {
        if (root == null) return 0;

        Map<Node<T>, Integer> levelMap = new HashMap<>();
        Queue<Node<T>> queue = new LinkedList<>();
        queue.add(root);
        levelMap.put(root, 0);

        int maxNodes = 0;
        Map<Integer, Integer> levelCount = new HashMap<>();

        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            int level = levelMap.get(node);
            levelCount.put(level, levelCount.getOrDefault(level, 0) + 1);
            maxNodes = Math.max(maxNodes, levelCount.get(level));

            for (Edge<T> edge : graph.getIncomingEdges(node)) {
                Node<T> target = edge.source();
                if (!levelMap.containsKey(target)) {
                    levelMap.put(target, level + 1);
                    queue.add(target);
                }
            }
        }

        return maxNodes;
    }

    public int determineDepth(Graph<T> graph, Node<T> root) {
        Set<Node<T>> visited = new HashSet<>();
        Map<Node<T>, Integer> depthMap = new HashMap<>();

        return determineDepth(graph, root, visited, depthMap);
    }

    private int determineDepth(Graph<T> graph, Node<T> node, Set<Node<T>> visited, Map<Node<T>, Integer> depthMap) {
        if (depthMap.containsKey(node)) return depthMap.get(node);
        if (visited.contains(node)) return 0;

        visited.add(node);
        int maxDepth = 0;

        for (Edge<T> edge : graph.getIncomingEdges(node)) {
            Node<T> target = edge.source();
            maxDepth = Math.max(maxDepth, determineDepth(graph, target, visited, depthMap));
        }

        visited.remove(node);
        int depth = maxDepth + 1;
        depthMap.put(node, depth);

        return depth;
    }
}
