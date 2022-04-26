package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElevationProfileComputerTest {

    @Test
    void elevationProfileWorksProperlyOnTrivialValue(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.constant(Float.NaN);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        DoubleUnaryOperator profile3 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 6, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(18, new float[]{0,0,0, 0,0,0,0});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1, elevationProfile1);
    }
    @Test
    void elevationProfileWorksProperly(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{3, 2, 3, 4, 6, 7, 9}, 6);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile2));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MAX_N), 6, profile2));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(18, new float[]{3, 3,3, 4, 9, 9, 9});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 3);
        assertEquals(expected1, elevationProfile1);
    }

    @Test
    void elevationProfileWorksProperlyOnSomeValues() {
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{4, 5, 2, 2, 4, 7, 6}, 6);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{3, 2, 4.5f, 4.5f}, 3);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 4, profile2));
        edges.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 3, profile3));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(13, new float[]{4, 5, 2, 2, 4, 7, 6, 5.25f, 4.5f, 3.75f, 3, 2, 4.5f, 4.5f});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);

        assertEquals(expected1, elevationProfile1);
    }

    @Test
    void elevationProfileWorksProperlyOnNonMultipleLengthValuesEtLaDaronneDeJeanJSPLequelEstUneMILF() {

    }

    @Test
    void elevationProfileWorkOnDoubleNaNProfile(){
        List<Edge> edges = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{4, 5, 2, 2, 4, 7, 6}, 6);
        DoubleUnaryOperator profile2 = Functions.constant(Float.NaN);
        DoubleUnaryOperator profile21 = Functions.constant(Float.NaN);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{3, 2, 4.5f, 4.5f}, 3);
        edges.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 6, profile1));
        edges.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2, profile2));
        edges.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 2, profile21));
        edges.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 3, profile3));
        SingleRoute route = new SingleRoute(edges);

        ElevationProfile expected1 = new ElevationProfile(13, new float[]{4, 5, 2, 2, 4, 7, 6, 5.25f, 4.5f, 3.75f, 3, 2, 4.5f, 4.5f});
        ElevationProfile elevationProfile1 = ElevationProfileComputer.elevationProfile(route, 1);

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
}
