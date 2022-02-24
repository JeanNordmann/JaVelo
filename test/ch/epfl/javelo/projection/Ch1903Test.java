package ch.epfl.javelo.projection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Ch1903Test {
    private static final double DELTA = 1e-7;


    @Test
    public void eWorksOnKnownValues() {
        var actual1 = Ch1903.e(Math.toRadians(7), Math.toRadians(47));
        var expected1 = 2566639.3048922247;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = Ch1903.e(Math.toRadians(8.1), Math.toRadians(46.1));
        var expected2 = 2651143.455888617;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = Ch1903.e(Math.toRadians(9.23), Math.toRadians(46.23));
        var expected3 = 2738187.9209433054;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = Ch1903.e(Math.toRadians(10.456), Math.toRadians(46.456));
        var expected4 = 2831760.145032002;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = Ch1903.e(Math.toRadians(6.5), Math.toRadians(46.5));
        var expected5 = 2527946.5323944297;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = Ch1903.e(Math.toRadians(7.56789), Math.toRadians(47.56789));
        var expected6 = 2609727.6473976434;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    public void nWorksOnKnownValues() {
        var actual1 = Ch1903.n(Math.toRadians(7), Math.toRadians(47));
        var expected1 = 1205531.9175609266;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = Ch1903.n(Math.toRadians(8.1), Math.toRadians(46.1));
        var expected2 = 1105603.2393465657;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = Ch1903.n(Math.toRadians(9.23), Math.toRadians(46.23));
        var expected3 = 1121416.662587053;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = Ch1903.n(Math.toRadians(10.456), Math.toRadians(46.456));
        var expected4 = 1149420.4005212034;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = Ch1903.n(Math.toRadians(6.5), Math.toRadians(46.5));
        var expected5 = 1150286.400502799;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = Ch1903.n(Math.toRadians(7.56789), Math.toRadians(47.56789));
        var expected6 = 1268583.970220614;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    public void lonWorksOnKnownValues() {
      var actual1 = Ch1903.lon(2600000, 1200000);
        var expected1 = 0.12982871138918287;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = Ch1903.lon(2700000, 1200000);
        var expected2 = 0.15275334931474058;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = Ch1903.lon(2512345, 1123456);
        var expected3 = 0.10998789612608173;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = Ch1903.lon(2712345, 1298765);
        var expected4 = 0.15601548084110384;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = Ch1903.lon(2800000, 1199999);
        var expected5 = 0.17566529683995633;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = Ch1903.lon(2600000, 1100000);
        var expected6 = 0.12982871138918287;
        assertEquals(expected6, actual6, DELTA);
    }

    @Test
    public void latWorksOnKnownValues() {
      var actual1 = Ch1903.lat(2600000, 1200000);
        var expected1 = 0.8194509527598063;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = Ch1903.lat(2700000, 1200000);
        var expected2 = 0.8193195789181267;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = Ch1903.lat(2512345, 1123456);
        var expected3 = 0.8073337829590987;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = Ch1903.lat(2712345, 1298765);
        var expected4 = 0.8347862855465886;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = Ch1903.lat(2800000, 1199999);
        var expected5 = 0.8189253004839152;
        assertEquals(expected5, actual5, DELTA);

        var actual6 = Ch1903.lat(2600000, 1100000);
        var expected6 = 0.8037508202024347;
        assertEquals(expected6, actual6, DELTA);
    }
}
