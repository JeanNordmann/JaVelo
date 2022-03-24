package ch.epfl.javelo.routing;

import ch.epfl.javelo.data.Graph;

public class RouteComputer {

    private final Graph graph;
    private final CostFunction costFunction;

    RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

}
