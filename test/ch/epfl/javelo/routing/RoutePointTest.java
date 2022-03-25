package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {
    @Test
    void withPositionShiftedByWorksProperly(){
        RoutePoint routePoint = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 14, 10);
        assertEquals(new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 4, 10), routePoint.withPositionShiftedBy(-10));
    }

    @Test
    void minWorksProperly(){
        RoutePoint routePointThis = new RoutePoint(new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N), 12345, 20000);
        RoutePoint routePointThat = new RoutePoint(new PointCh(SwissBounds.MAX_E, SwissBounds.MAX_N), 12346, 15000);
        assertEquals(routePointThat, routePointThis.min(routePointThat));
        assertEquals(routePointThis, routePointThis.min(RoutePoint.NONE));
    }
}