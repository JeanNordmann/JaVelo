package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class SingleRouteTest {
    @Test
    void constructorThrowsOnEmptyArgument(){
        assertThrows(IllegalArgumentException.class, () ->{
            new SingleRoute(List.of());
        });
    }

    @Test
    void lengthWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile = Functions.constant(0);
        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile));
        SingleRoute route = new SingleRoute(list);
        assertEquals(21, route.length());
    }

    @Test
    void pointsWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile = Functions.constant(0);
        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile));
        SingleRoute route = new SingleRoute(list);
        List<PointCh> points = new ArrayList<>();
        points.add(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N));
        points.add(new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N));
        points.add(new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4));
        points.add(new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10));
        assertEquals(points, route.points());
    }

    @Test
    void pointAtWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile = Functions.constant(0);
        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile));
        SingleRoute route = new SingleRoute(list);

        PointCh point1 = new PointCh(SwissBounds.MIN_E + 4, SwissBounds.MIN_N);
        assertEquals(point1, route.pointAt(4));

        PointCh point2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        assertEquals(point2, route.pointAt(0));
        assertEquals(point2, route.pointAt(-10));

        PointCh point3 = new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10);
        assertEquals(point3, route.pointAt(1000));
        assertEquals(point3, route.pointAt(21));

        PointCh point4 = new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 5);
        assertEquals(point4, route.pointAt(16));
    }

    @Test
    void elevationAtWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]{2, 1, 2, 3, 2, 1, 0.5f, 1, 1.5f , 1, 0.5f}, 10);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]{3, 4, 5, 6, 6, 7}, 5);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]{187, 156, 134, 3, 456, 133, 34}, 6);

        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile1));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile2));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile3));
        SingleRoute route = new SingleRoute(list);

        assertEquals(3, route.elevationAt(3));
        assertEquals(2, route.elevationAt(-12));
        assertEquals(34, route.elevationAt(1000));
        assertEquals(145, route.elevationAt(16.5));


    }

    @Test
    void nodeClosestToWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile = Functions.constant(0);
        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile));
        SingleRoute route = new SingleRoute(list);


        assertEquals(2, route.nodeClosestTo(14));
        assertEquals(2, route.nodeClosestTo(17));
        assertEquals(0, route.nodeClosestTo(-10));
        assertEquals(3, route.nodeClosestTo(1234));
        assertEquals(1, route.nodeClosestTo(12.5));
    }

    @Test
    void pointClosestToWorksProperly(){
        List<Edge> list = new ArrayList<>();
        DoubleUnaryOperator profile = Functions.constant(0);
        list.add(new Edge(0, 1, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), 10, profile));
        list.add(new Edge(1, 2, new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), 5, profile));
        list.add(new Edge(2, 3, new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 4), new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 6, profile));
        SingleRoute route = new SingleRoute(list);

        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 13, SwissBounds.MIN_N + 10), 21, 5), route.pointClosestTo(new PointCh(SwissBounds.MIN_E + 8, SwissBounds.MIN_N + 10)));
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MIN_N), 6, 4), route.pointClosestTo(new PointCh(SwissBounds.MIN_E + 6, SwissBounds.MIN_N + 4)));
    }
}