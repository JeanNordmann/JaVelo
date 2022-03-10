package ch.epfl.javelo.projection;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTest {
    private static final double DELTA = 1e-7;

    @Test
    void pointWebMercatorThrowsOnInvalidCoordinates() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PointWebMercator(Math.nextDown(0), 0.5));
        assertThrows(
                IllegalArgumentException.class,
                () -> new PointWebMercator(Math.nextUp(1), 0.5));
        assertThrows(
                IllegalArgumentException.class,
                () -> new PointWebMercator(0.5, Math.nextDown(0)));
        assertThrows(
                IllegalArgumentException.class,
                () -> new PointWebMercator(0.5, Math.nextUp(1)));
    }

    @Test
    void pointWebMercatorDoesNotThrowOnValidCoordinates() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble();
            var y = rng.nextDouble();
            assertDoesNotThrow(() -> new PointWebMercator(x, y));
        }
        assertDoesNotThrow(() -> new PointWebMercator(0, 0));
        assertDoesNotThrow(() -> new PointWebMercator(1, 1));
    }

    @Test
    void pointWebMercatorOfAndXYAtZoomLevelAreInverse() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var z = rng.nextInt(20);
            var maxXY = Math.scalb(1d, z + 8);
            var x = rng.nextDouble(maxXY);
            var y = rng.nextDouble(maxXY);
            var p = PointWebMercator.of(z, x, y);
            assertEquals(x, p.xAtZoomLevel(z), 1e-8);
            assertEquals(y, p.yAtZoomLevel(z), 1e-8);
        }
    }

    @Test
    void pointWebMercatorOfPointChWorksOnKnownValues() {
        var p1 = PointWebMercator
                .ofPointCh(new PointCh(2_600_000, 1_200_000));
        var actualX1 = p1.x();
        var actualY1 = p1.y();
        var expectedX1 = 0.5206628811728395;
        var expectedY1 = 0.3519253787614047;
        assertEquals(expectedX1, actualX1, DELTA);
        assertEquals(expectedY1, actualY1, DELTA);

        var p2 = PointWebMercator
                .ofPointCh(new PointCh(2_533_132, 1_152_206));
        var actualX2 = p2.x();
        var actualY2 = p2.y();
        var expectedX2 = 0.5182423951719917;
        var expectedY2 = 0.3536813812215855;
        assertEquals(expectedX2, actualX2, DELTA);
        assertEquals(expectedY2, actualY2, DELTA);
    }

    @Test
    void pointWebMercatorLonWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0, 0).lon();
        var expected1 = -3.141592653589793;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.25, 0.25).lon();
        var expected2 = -1.5707963267948966;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(0.5, 0.5).lon();
        var expected3 = 0.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.75, 0.75).lon();
        var expected4 = 1.5707963267948966;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = new PointWebMercator(1, 1).lon();
        var expected5 = 3.141592653589793;
        assertEquals(expected5, actual5, DELTA);
    }

    @Test
    void pointWebMercatorLatWorksOnKnownValues() {
        var actual1 = new PointWebMercator(0, 0).lat();
        var expected1 = 1.4844222297453324;
        assertEquals(expected1, actual1, DELTA);

        var actual2 = new PointWebMercator(0.25, 0.25).lat();
        var expected2 = 1.1608753909688045;
        assertEquals(expected2, actual2, DELTA);

        var actual3 = new PointWebMercator(0.5, 0.5).lat();
        var expected3 = 0.0;
        assertEquals(expected3, actual3, DELTA);

        var actual4 = new PointWebMercator(0.75, 0.75).lat();
        var expected4 = -1.1608753909688045;
        assertEquals(expected4, actual4, DELTA);

        var actual5 = new PointWebMercator(1, 1).lat();
        var expected5 = -1.4844222297453324;
        assertEquals(expected5, actual5, DELTA);
    }

    @Test
    void pointWebMercatorToPointChWorksOnKnownValues() {
        var p1 = new PointWebMercator(0.5206628811728395, 0.3519253787614047)
                .toPointCh();
        var actualE1 = p1.e();
        var actualN1 = p1.n();
        var expectedE1 = 2600000.346333851;
        var expectedN1 = 1199999.8308213386;
        assertEquals(expectedE1, actualE1, DELTA);
        assertEquals(expectedN1, actualN1, DELTA);

        var p2 = new PointWebMercator(0.5182423951719917, 0.3536813812215855)
                .toPointCh();
        var actualE2 = p2.e();
        var actualN2 = p2.n();
        var expectedE2 = 2533131.6362025095;
        var expectedN2 = 1152206.8789113415;
        assertEquals(expectedE2, actualE2, DELTA);
        assertEquals(expectedN2, actualN2, DELTA);
    }
}