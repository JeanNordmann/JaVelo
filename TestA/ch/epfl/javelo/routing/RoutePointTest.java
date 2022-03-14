package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.RoutePoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoutePointTest {

    @Test
    void routePointWorkWithPositionShiftedBy() {
        assertEquals(new RoutePoint(new PointCh(2600000, 1180000), 234, 20000),
                new RoutePoint(new PointCh(2600000, 1180000), 200, 20000).withPositionShiftedBy(34));
    }

    @Test
    void routePointWorkOnMin1(){
        RoutePoint rt1 = new RoutePoint(new PointCh(2600000, 1180000), 10, 1000);
        RoutePoint rt2 = new RoutePoint(new PointCh(2600000, 1180000), 10, 2000);
        RoutePoint rt3 = new RoutePoint(new PointCh(2600000, 1180000), 10, 3000);
        assertEquals(rt1, rt1.min(rt2));
        assertEquals(rt1, rt1.min(rt3));
        assertEquals(rt2, rt2.min(rt3));
    }

    @Test
    void routePointWorkOnMin2(){
        RoutePoint rt1 = new RoutePoint(new PointCh(2600000, 1180000), 10, 1000);
        RoutePoint rt2 = new RoutePoint(new PointCh(2600000, 1180000), 10, 2000);
        RoutePoint rt3 = new RoutePoint(new PointCh(2600000, 1180000), 10, 3000);
        assertEquals(rt1, rt1.min(new PointCh(2600000, 1180000), 10, 2000));
        assertEquals(rt1, rt1.min(new PointCh(2600000, 1180000), 10, 3000));
        assertEquals(rt2, rt2.min(new PointCh(2600000, 1180000), 10, 3000));
    }
}
