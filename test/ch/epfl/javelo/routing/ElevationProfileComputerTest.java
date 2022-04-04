package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.TestManager.*;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {
    Graph loadLausanne() {
        try {
            Graph g = Graph.loadFrom(Path.of("lausanne"));
            return g;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getRandomEdgeId(Graph g, int nodeId) {
        return g.nodeOutEdgeId(nodeId, generateRandomIntInBounds(0, g.nodeOutDegree(nodeId) - 1));
    }


//    void testNodeSamples() {
//        Route r = createRandomRoute(2);
//        List<Edge> edges = r.edges();
//
//        Edge edge1 = edges.get(0);
//        Edge edge2 = edges.get(1);
//
//        System.out.println(r.elevationAt(edge1.length()));
//        System.out.println(r.elevationAt(edge1.length() + 0.1));
//    }

    Route createRandomRoute(int length) {
        Edge[] edges = new Edge[length];
        Graph g = loadLausanne();

        // start with random node
        int startNodeId = generateRandomIntInBounds(0, g.nodeCount() - 1);
        int edgeOutId = getRandomEdgeId(g, startNodeId);

        Edge e = Edge.of(g, edgeOutId, startNodeId, g.edgeTargetNodeId(edgeOutId));
        edges[0] = e;

        // System.out.println("ID= " + edgeOutId + "--" + e);

        for (int i = 1; i < length; i++) {
            int newNodeId = edges[i - 1].toNodeId();
            int newEdgeId = getRandomEdgeId(g, newNodeId);

            edges[i] = Edge.of(g, newEdgeId, newNodeId, g.edgeTargetNodeId(newEdgeId));

            // System.out.println("ID= " + newEdgeId + "--" + edges[i]);
        }

        return new SingleRoute(List.of(edges));
    }

    Route createKnownRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 5, 7, 8, 9}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{3, 7, 8, 2}, 3);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 6, profile3));
        edges.add(new Edge(2, 0, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 3, profile2));
        SingleRoute route = new SingleRoute(edges);

        return route;
    }

    private PointCh relPoint(double e, double n) {
        double newE = Math2.clamp(SwissBounds.MIN_E, SwissBounds.MIN_E + e, SwissBounds.MAX_E);
        double newN = Math2.clamp(SwissBounds.MIN_N, SwissBounds.MIN_N + e, SwissBounds.MAX_N);

        return new PointCh(newE, newN);
    }

    Route createConstantTestRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{0, 2, 4, 2, 1}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{1, 0, 2, 0, 2}, 2);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);

        edges.add(new Edge(0, 1, relPoint(1, 1), relPoint(10, 10), 4, profile1));
        edges.add(new Edge(1, 2, relPoint(10, 10), relPoint(2, 20), 2, profile2));
        edges.add(new Edge(2, 0, relPoint(20, 2), relPoint(4, 4), 1, profile3));

        return new SingleRoute(edges);
    }

    Route createInterpolatedTestRoute() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{0, 2, 4, 2, 1}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{1, 0, 2, 0, 2}, 2);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);


        edges.add(new Edge(0, 1, relPoint(1, 1), relPoint(10, 10), 4, profile1));
        edges.add(new Edge(1, 2, relPoint(10, 10), relPoint(2, 20), 2, profile2));
        edges.add(new Edge(2, 0, relPoint(20, 2), relPoint(4, 4), 1, profile3));
        edges.add(new Edge(0, 3, relPoint(1, 1), relPoint(6, 1), 2, profile2));

        return new SingleRoute(edges);
    }

    Route createKnownLausanneRoute() {
        List<Edge> edges = new ArrayList<>();

        Graph g = loadLausanne();
        int[][] edgeInfo = new int[][] {{108256, 51244, 51245}, {108258, 51245, 51247}, {108265, 51247, 51246}, {108260, 51246, 51247}, {108263, 51247, 51245}, {108258, 51245, 51247}, {108262, 51247, 51253}, {108276, 51253, 51193}, {108145, 51193, 51192}, {108144, 51192, 51193}};

        for(int[] e : edgeInfo) {
            edges.add(Edge.of(g, e[0], e[1], e[2]));
        }

        return new SingleRoute(edges);
    }

    @Test
    void elevationProfileDoesThrowException() {
        Route r = createKnownRoute();

        // negative values
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(r, -3);
        });

        // positive (=0) values
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(r, 0);
        });

        // regular (>0) values
        assertDoesNotThrow(() -> {
            ElevationProfileComputer.elevationProfile(r, 1);
        });
    }

    @Test
    void elevationProfileDoesWorkWithKnownRoute() {
        Route r = createConstantTestRoute();

        float[] expectedSamples = {0, 1, 2, 3, 4, 3, 2, 1.5f, 1, 0, 2, 0, 2, 2, 2}; // step : 0.5, use this to double-check stuff.

        // System.out.println(r.length() );
        ElevationProfile elev = ElevationProfileComputer.elevationProfile(r, 0.5);

        // System.out.println(r.length() / 14 );
        assertEquals(r.length(), elev.length());
        assertEquals(0, elev.minElevation(), DOUBLE_DELTA);
        assertEquals(4, elev.maxElevation(), DOUBLE_DELTA);

        assertEquals(8, elev.totalAscent(), DOUBLE_DELTA);
        assertEquals(6, elev.totalDescent(), DOUBLE_DELTA);

        assertEquals(4, elev.elevationAt(2), DOUBLE_DELTA);
        assertEquals(2, elev.elevationAt(7), DOUBLE_DELTA);
    }

    @Test
    void elevationProfileDoesWorkWithInterpolatedRoute() {
        Route r = createInterpolatedTestRoute();

        float[] expectedSamples = {0, 1, 2, 3, 4, 3, 2, 1.5f, 1, 1, 0, 2, 0, 2, 1.5f, 1, 0, 2, 0, 2}; // step : 0.5, use this to double-check stuff.

        ElevationProfile elev = ElevationProfileComputer.elevationProfile(r, 0.5);

        assertEquals(r.length(), elev.length());
        assertEquals(0, elev.minElevation(), DOUBLE_DELTA);
        assertEquals(4, elev.maxElevation(), DOUBLE_DELTA);

        assertEquals(12, elev.totalAscent(), DOUBLE_DELTA);
        assertEquals(10, elev.totalDescent(), DOUBLE_DELTA);

        assertEquals(1.5f, elev.elevationAt(6.5), DOUBLE_DELTA);
        assertEquals(1.5f, elev.elevationAt(3.5), DOUBLE_DELTA);

    }
}