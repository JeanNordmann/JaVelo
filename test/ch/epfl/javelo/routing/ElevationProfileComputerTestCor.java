package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElevationProfileComputerTestCor {
    @Test
    void elevationProfileComputerThrowsWithZeroMaxStepLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            elevationProfile(new FakeRoute(), 0);
        });
    }

    @Test
    void elevationProfileComputerWorksWithCompletelyUnknownProfile() {
        for (int i = 1; i < 10; i += 1) {
            var route = new FakeRoute(i, x -> Double.NaN);
            var profile = elevationProfile(route, 500);

            assertEquals(route.length(), profile.length());
            assertEquals(0, profile.minElevation());
            assertEquals(0, profile.maxElevation());
            assertEquals(0, profile.totalAscent());
            assertEquals(0, profile.totalDescent());
            assertEquals(0, profile.elevationAt(Math.nextDown(0)));
            assertEquals(0, profile.elevationAt(500));
            assertEquals(0, profile.elevationAt(Math.nextUp(route.length())));
        }
    }

    @Test
    void elevationProfileComputerWorksWithHoleAtBeginning() {
        var elevation = 500d;
        var route = new FakeRoute(1, x -> x < FakeRoute.EDGE_LENGTH / 2d ? Double.NaN : elevation);
        var profile = elevationProfile(route, 1);
        assertEquals(elevation, profile.minElevation());
        assertEquals(elevation, profile.maxElevation());
        assertEquals(0, profile.totalAscent());
        assertEquals(0, profile.totalDescent());
        for (double p = 0; p < route.length(); p += 1) {
            assertEquals(elevation, profile.elevationAt(p));
        }
    }

    @Test
    void elevationProfileComputerWorksWithHoleAtEnd() {
        var elevation = 500d;
        var route = new FakeRoute(1, x -> x > FakeRoute.EDGE_LENGTH / 2d ? Double.NaN : elevation);
        var profile = elevationProfile(route, 1);
        assertEquals(elevation, profile.minElevation());
        assertEquals(elevation, profile.maxElevation());
        assertEquals(0, profile.totalAscent());
        assertEquals(0, profile.totalDescent());
        for (double p = 0; p < route.length(); p += 1) {
            assertEquals(elevation, profile.elevationAt(p));
        }
    }

    @Test
    void elevationProfileComputerWorksWithMissingValuesInTheMiddle() {
        var startElevation = 100;
        var endElevation = startElevation + FakeRoute.EDGE_LENGTH;
        DoubleUnaryOperator edgeProfile = x -> {
            if (x < 0.1)
                return startElevation;
            else if (x > FakeRoute.EDGE_LENGTH - 0.1)
                return endElevation;
            else
                return Double.NaN;
        };
        var route = new FakeRoute(1, edgeProfile);
        var profile = elevationProfile(route, 1);
        assertEquals(endElevation - startElevation, profile.totalAscent(), 1);
        assertEquals(0, profile.totalDescent());
        assertEquals(startElevation, profile.minElevation());
        assertEquals(endElevation, profile.maxElevation());
        for (double p = 0; p < route.length(); p += 1) {
            var elevation = startElevation + (endElevation - startElevation) * (p / FakeRoute.EDGE_LENGTH);
            assertEquals(elevation, profile.elevationAt(p), 1e-2);
        }
    }

    @Test
    void elevationProfileComputerWorksWithHolesOfDifferentLengths() {
        var samples = new double[1001];

        var holeLength = 0;
        var remainingNaNsToInsert = 0;
        for (int i = 0; i < samples.length; i += 1) {
            if (remainingNaNsToInsert == 0) {
                samples[i] = i;
                holeLength += 1;
                remainingNaNsToInsert = holeLength;
            } else {
                samples[i] = Double.NaN;
                remainingNaNsToInsert -= 1;
            }
        }
        samples[samples.length - 1] = samples.length - 1;

        var route = new FakeRoute(1, x -> samples[(int) Math.rint(x)]);
        var profile = elevationProfile(route, 1);
        for (int i = 0; i < FakeRoute.EDGE_LENGTH; i += 1)
            assertEquals(i, profile.elevationAt(i), 1e-4);
    }

    @Test
    void elevationProfileComputerWorksWithFullyKnownProfile() {
        DoubleUnaryOperator edgeProfile =
                x -> 600d + 500d * Math.sin(2d * Math.PI / FakeRoute.EDGE_LENGTH);
        var route = new FakeRoute(1, edgeProfile);
        var profile = elevationProfile(route, 0.1);
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var p = rng.nextDouble(0, route.length());
            assertEquals(edgeProfile.applyAsDouble(p), profile.elevationAt(p), 1e-3);
        }
    }

    private static final class FakeRoute implements Route {
        private static final double ORIGIN_E = 2_600_000;
        private static final double ORIGIN_N = 1_200_000;
        private static final double EDGE_LENGTH = 1_000;

        private final int edgesCount;
        private final DoubleUnaryOperator edgeProfile;

        public FakeRoute(int edgesCount, DoubleUnaryOperator edgeProfile) {
            this.edgesCount = edgesCount;
            this.edgeProfile = edgeProfile;
        }

        public FakeRoute() {
            this(1, x -> Double.NaN);
        }

        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return Math.nextDown(edgesCount * EDGE_LENGTH);
        }

        @Override
        public List<Edge> edges() {
            var points = points();
            var edges = new ArrayList<Edge>(edgesCount);
            for (int i = 0; i < edgesCount; i += 1) {
                var p1 = points.get(i);
                var p2 = points.get(i + 1);
                edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, edgeProfile));
            }
            return Collections.unmodifiableList(edges);
        }

        @Override
        public List<PointCh> points() {
            var points = new ArrayList<PointCh>(edgesCount + 1);
            for (int i = 0; i < edgesCount + 1; i += 1)
                points.add(new PointCh(ORIGIN_E + i * EDGE_LENGTH, ORIGIN_N));
            return Collections.unmodifiableList(points);
        }

        @Override
        public PointCh pointAt(double position) {
            position = max(0, min(position, length()));
            return new PointCh(ORIGIN_E + position, ORIGIN_N);
        }

        @Override
        public double elevationAt(double position) {
            position = max(0, min(position, length()));
            return edgeProfile.applyAsDouble(position % EDGE_LENGTH);
        }

        @Override
        public int nodeClosestTo(double position) {
            position = max(0, min(position, length()));
            return (int) Math.rint(position / EDGE_LENGTH);
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            if (point.e() <= ORIGIN_E) {
                var origin = new PointCh(ORIGIN_E, ORIGIN_N);
                return new RoutePoint(origin, 0, point.distanceTo(origin));
            } else if (point.e() >= ORIGIN_E + edgesCount * EDGE_LENGTH) {
                var lastPoint = new PointCh(ORIGIN_E + edgesCount * EDGE_LENGTH, ORIGIN_N);
                return new RoutePoint(lastPoint, 0, point.distanceTo(lastPoint));
            } else {
                var p = new PointCh(point.e(), ORIGIN_N);
                return new RoutePoint(p, point.e() - ORIGIN_E, point.n() - ORIGIN_N);
            }
        }
    }
}