package com.bawnorton.trulyrandom.client.loot.graph;

import com.bawnorton.trulyrandom.TrulyRandom;
import com.bawnorton.trulyrandom.client.loot.graph.element.GraphElement;
import com.bawnorton.trulyrandom.client.loot.graph.element.GraphElementCollection;
import it.unimi.dsi.fastutil.Pair;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.builder.GraphBuilder;
import org.joml.Vector2f;
import org.jungrapht.visualization.layout.algorithms.HierarchicalMinCrossLayoutAlgorithm;
import org.jungrapht.visualization.layout.algorithms.sugiyama.Layering;
import org.jungrapht.visualization.layout.event.LayoutStateChange;
import org.jungrapht.visualization.layout.model.LayoutModel;
import org.jungrapht.visualization.layout.model.Point;
import org.jungrapht.visualization.layout.model.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LootGraph {
    private final GraphElement root;
    private final Graph<GraphElement, DefaultEdge> graph;
    private final List<ChangeListener> listeners = new ArrayList<>();
    private Map<GraphElement, Vector2f> posMap;
    private final Object mutex = new Object();

    public LootGraph(GraphElement root) {
        GraphBuilder<GraphElement, DefaultEdge, ? extends SimpleDirectedGraph<GraphElement, DefaultEdge>> graphBuilder = SimpleDirectedGraph.createBuilder(DefaultEdge.class);
        this.root = root.supplyGraph(graphBuilder);
        this.graph = graphBuilder.build();
        this.posMap = layout();
    }

    @SuppressWarnings("unchecked")
    private Map<GraphElement, Vector2f> layout() {
        HierarchicalMinCrossLayoutAlgorithm<GraphElement, DefaultEdge> algorithm = HierarchicalMinCrossLayoutAlgorithm.<GraphElement, DefaultEdge>edgeAwareBuilder()
                .vertexBoundsFunction(vertex -> new Rectangle(0, 0, 64, 64))
                .layering(Layering.LONGEST_PATH)
                .straightenEdges(true)
                .threaded(true)
                .build();
        LayoutModel<GraphElement> layoutModel = LayoutModel.<GraphElement>builder()
                .graph(graph)
                .size(1, 1)
                .build();
        LayoutStateChange.Support support = layoutModel.getLayoutStateChangeSupport();
        support.addLayoutStateChangeListener(event -> {
            synchronized (mutex) {
                posMap = formatPositions(event.layoutModel);
                listeners.forEach(listener -> listener.onChange(this));
                Map<Vector2f, List<GraphElement>> overlapping = new HashMap<>();
                for (GraphElement graphElement : graph.vertexSet()) {
                    overlapping.computeIfAbsent(getPos(graphElement), k -> new ArrayList<>()).add(graphElement);
                }
                overlapping.entrySet().stream()
                        .filter(entry -> entry.getValue().size() > 1)
                        .map(entry -> Pair.of(new GraphElementCollection(entry.getValue()), entry.getKey()))
                        .forEach(pair -> {
                            GraphElementCollection collection = pair.first();
                            graph.addVertex(collection);
                            posMap.put(collection, pair.second());
                            Set<DefaultEdge> outEdges = new HashSet<>();
                            Set<DefaultEdge> inEdges = new HashSet<>();
                            for (GraphElement graphElement : collection) {
                                outEdges.addAll(graph.outgoingEdgesOf(graphElement));
                                inEdges.addAll(graph.incomingEdgesOf(graphElement));
                                graph.removeVertex(graphElement);
                                posMap.remove(graphElement);
                            }
                            for (DefaultEdge outEdge : outEdges) {
                                GraphElement to = graph.getEdgeTarget(outEdge);
                                graph.addEdge(collection, to);
                            }

                            for (DefaultEdge inEdge : inEdges) {
                                GraphElement from = graph.getEdgeSource(inEdge);
                                graph.addEdge(from, collection);
                            }
                        });
            }
        });
        algorithm.visit(layoutModel);
        return formatPositions(layoutModel);
    }

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    private Map<GraphElement, Vector2f> formatPositions(LayoutModel<GraphElement> layoutModel) {
        return layoutModel.getLocations().entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), mapPoint(entry.getValue())))
                .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    public void forEachVertex(Consumer<GraphElement> action) {
        synchronized (mutex) {
            graph.vertexSet().forEach(action);
        }
    }

    public void forEachEdge(BiConsumer<GraphElement, GraphElement> action) {
        synchronized (mutex) {
            graph.edgeSet().forEach(edge -> {
                GraphElement source = graph.getEdgeSource(edge);
                GraphElement target = graph.getEdgeTarget(edge);
                action.accept(source, target);
            });
        }
    }

    public Vector2f getRootPos() {
        return getPos(root);
    }

    public Vector2f getPos(GraphElement element) {
        return posMap.getOrDefault(element, new Vector2f());
    }

    private Vector2f mapPoint(Point p) {
        return new Vector2f((float) p.y / 2, (float) p.x);
    }

    public interface ChangeListener {
        void onChange(LootGraph graph);
    }
}
