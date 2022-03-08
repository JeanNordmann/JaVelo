package ch.epfl.javelo.projection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointChTest {
    public static final double DELTA = 1e-7;

    @Test
    void pointChConstructorThrowsOnInvalidCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PointCh(2484999, 1200000);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointCh(2834001, 1200000);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointCh(2600000, 1074999);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new PointCh(2600000, 1296001);
        });
    }

    @Test
    void pointChConstructorWorksOnValidCoordinates() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var e = rng.nextDouble(2485000, 2834000);
            var n = rng.nextDouble(1075000, 1296000);
            new PointCh(e, n);
        }
    }

    @Test
    void pointChSquaredDistanceToWorksOnKnownValues() {
        var actual1 = new PointCh(2600000, 1200000)
                .squaredDistanceTo(new PointCh(2600000, 1200000));
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointCh(2600000, 1200000)
                .squaredDistanceTo(new PointCh(2600100, 1200000));
        var expected2 = 10000.0;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointCh(2600000, 1200000)
                .squaredDistanceTo(new PointCh(2600000, 1200100));
        var expected3 = 10000.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointCh(2600000, 1200000)
                .squaredDistanceTo(new PointCh(2601234, 1201234));
        var expected4 = 3045512.0;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointChDistanceToWorksOnKnownValues() {
        var actual1 = new PointCh(2600000, 1200000)
                .distanceTo(new PointCh(2600000, 1200000));
        var expected1 = 0.0;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointCh(2600000, 1200000)
                .distanceTo(new PointCh(2485001, 1075001));
        var expected2 = 169851.4645270979;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointCh(2485001, 1075001)
                .distanceTo(new PointCh(2833999, 1295999));
        var expected3 = 413085.60857042694;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointCh(2700000, 1100000)
                .distanceTo(new PointCh(2833999, 1295999));
        var expected4 = 237426.4938923203;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointChLonWorksWithKnownValues() {
        var actual1 = new PointCh(2600000, 1200000).lon();
        var expected1 = 0.12982871138918287;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointCh(2485001, 1075001).lon();
        var expected2 = 0.10400660553294673;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointCh(2833999, 1295999).lon();
        var expected3 = 0.18432563294260465;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointCh(2700000, 1100000).lon();
        var expected4 = 0.15237595870983656;
        assertEquals(expected4, actual4, DELTA);
    }

    @Test
    void pointChLatWorksWithKnownValues() {
        var actual1 = new PointCh(2600000, 1200000).lat();
        var expected1 = 0.8194509527598063;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointCh(2485001, 1075001).lat();
        var expected2 = 0.7996558818339784;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointCh(2833999, 1295999).lat();
        var expected3 = 0.8337899321808625;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointCh(2700000, 1100000).lat();
        var expected4 = 0.8036216134779096;
        assertEquals(expected4, actual4, DELTA);
    }
}
