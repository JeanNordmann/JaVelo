package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static org.junit.jupiter.api.Assertions.*;

class RoutePointTest {
    @Test
    void routePointNoneIsDefinedCorrectly() {
        assertNull(RoutePoint.NONE.point());
        assertTrue(Double.isNaN(RoutePoint.NONE.position()));
        assertEquals(Double.POSITIVE_INFINITY, RoutePoint.NONE.distanceToReference());
    }

    @Test
    void routePointWithPositionShiftedShiftsPositionAndNothingElse() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e = 2_600_000 + rng.nextDouble(-50_000, 50_000);
            var n = 1_200_000 + rng.nextDouble(-50_000, 50_000);
            var pointCh = new PointCh(e, n);
            var position = rng.nextDouble(0, 200_000);
            var distanceToReference = rng.nextDouble(0, 1_000);
            var routePoint = new RoutePoint(pointCh, position, distanceToReference);
            var positionShift = rng.nextDouble(-position, 200_000);
            var routePointShifted = routePoint.withPositionShiftedBy(positionShift);
            assertEquals(pointCh, routePointShifted.point());
            assertEquals(distanceToReference, routePointShifted.distanceToReference());
            assertEquals(position + positionShift, routePointShifted.position());
        }
    }

    @Test
    void routePointMin1Works() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var point1 = randomRoutePoint(rng);
            var point2 = randomRoutePoint(rng);
            if (point1.distanceToReference() < point2.distanceToReference()) {
                assertEquals(point1, point1.min(point2));
                assertEquals(point1, point2.min(point1));
            } else if (point2.distanceToReference() < point1.distanceToReference()) {
                assertEquals(point2, point1.min(point2));
                assertEquals(point2, point2.min(point1));
            }
        }
    }

    @Test
    void routePointMin2Works() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var point1 = randomRoutePoint(rng);
            var point2 = randomRoutePoint(rng);
            if (point1.distanceToReference() < point2.distanceToReference()) {
                assertEquals(point1, point1.min(point2.point(), point2.position(), point2.distanceToReference()));
                assertNotEquals(point2, point2.min(point1.point(), point1.position(), point1.distanceToReference()));
            } else if (point2.distanceToReference() < point1.distanceToReference()) {
                assertEquals(point2, point1.min(point2.point(), point2.position(), point2.distanceToReference()));
                assertNotEquals(point1, point2.min(point1.point(), point1.position(), point2.distanceToReference()));
            }
        }
    }

    private RoutePoint randomRoutePoint(RandomGenerator rng) {
        var e = 2_600_000 + rng.nextDouble(-50_000, 50_000);
        var n = 1_200_000 + rng.nextDouble(-50_000, 50_000);
        var pointCh = new PointCh(e, n);
        var position = rng.nextDouble(0, 200_000);
        var distanceToReference = rng.nextDouble(0, 1_000);
        return new RoutePoint(pointCh, position, distanceToReference);
    }
}