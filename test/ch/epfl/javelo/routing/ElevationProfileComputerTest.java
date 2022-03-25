package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTest {
    @Test
    void elevationProfileWorksProperly() {
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{2, 1, 2, 3, 2, 1, 0.5f, 1, 1.5f, 1, 0.5f}, 10);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{0.5f, 4, 5, 6, 6, 7}, 5);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{7, 156, 134, 3, 456, 133, 34}, 6);

        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile1));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile2));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile3));
        SingleRoute route = new SingleRoute(list);

        ElevationProfile elevationProfile = ElevationProfileComputer.elevationProfile(route, 1);
        ElevationProfile expected = new ElevationProfile(21, new float[]{2, 1, 2, 3, 2,  1, 0.5f, 1, 1.5f, 1, 0.5f, 4, 5, 6, 6, 7, 156, 134, 3, 456, 133, 34});
        assertEquals(expected, elevationProfile);
    }

    @Test
    void elevationProfileThrowsOnNegativeStepLength(){
        assertThrows(IllegalArgumentException.class, () -> ElevationProfileComputer.elevationProfile(new SingleRoute(List.of()), 0));
    }

    @Test
    void elevationProfileWorksOnOnlyNan(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 10, profile1));
        SingleRoute route = new SingleRoute(edges);
        ElevationProfile expected1 = new ElevationProfile(10, new float[]{0, 0, 0, 0, 0});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1, elevationProfile1);
    }

    @Test
    void elevationProfileWorksOnNanAtTheBeginningAndEnd(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{3, 2, 3, 4, 6, 7, 9}, 6);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 6, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(18, new float[]{3, 3, 3, 4, 9, 9, 9});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1, elevationProfile1);

    }

    @Test
    void elevationProfileWorksOnNanInTheMiddle(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 5, 7, 8, 9}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{3, 7, 8, 2}, 3);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 6, profile3));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 3, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(13, new float[]{1, 5, 7, 8, 9, 8, 7, 6, 5, 4, 3, 7, 8, 2});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);
        assertEquals(expected1, elevationProfile1);
    }

    @Test
    void elevationProfileWorksInComplexProfile(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{1, 2, 5, 4, 3}, 4);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{5, 6, 5, 4}, 3);
        DoubleUnaryOperator profile3= Functions.sampled(new float[]{1, 2, 3}, 2);
        DoubleUnaryOperator profile4 = Functions.constant(Float.NaN);

        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 4, profile4));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 3, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 3, profile4));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E, SwissBounds.MAX_N), 2, profile3));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 4, profile4));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(20, new float[]{1, 2, 5, 4, 3, 3.5f, 4, 4.5f, 5, 6, 5, 4, 3, 2, 1, 2, 3, 3, 3, 3, 3});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);
        assertEquals(expected1, elevationProfile1);

        ElevationProfile expected2 = new ElevationProfile(20, new float[]{1, 5, 3, 4, 5, 5, 3, 1, 3, 3, 3});
        ElevationProfile elevationProfile2 = ElevationProfileComputer.elevationProfile(route, 2);
        assertEquals(expected2, elevationProfile2);

    }
}