package ch.epfl.javelo;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class Math2Test {
    private static final double DELTA = 1e-7;


    @Test
    void ceilDivThrowsOnNegativeX() {
        assertThrows(IllegalArgumentException.class, () -> {
            Math2.ceilDiv(-1, 2);
        });
    }

    @Test
    void ceilDivThrowsOnZeroY() {
        assertThrows(IllegalArgumentException.class, () -> {
            Math2.ceilDiv(1, 0);
        });
    }

    @Test
    void ceilDivWorksOnPositiveValues() {
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextInt(1000);
            var y = rng.nextInt(1, 1000);
            var expected = (int) Math.ceil((double) x / (double) y);
            var actual = Math2.ceilDiv(x, y);
            assertEquals(expected, actual);
        }
    }

    @Test
    void interpolateWorksWith0() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v1 = rng.nextDouble(-1000, 1000);
            var v2 = rng.nextDouble(-1000, 1000);
            assertEquals(v1, Math2.interpolate(v1, v2, 0), 1e-7);
        }
    }

    @Test
    void interpolateWorksWith1() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v1 = rng.nextDouble(-1000, 1000);
            var v2 = rng.nextDouble(-1000, 1000);
            assertEquals(v2, Math2.interpolate(v1, v2, 1), 1e-7);
        }
    }

    @Test
    void interpolateWorksWithRandomValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var y0 = rng.nextDouble(-1000, 1000);
            var y1 = rng.nextDouble(-1000, 1000);
            var x = rng.nextDouble(-20, 20);
            var y = Math2.interpolate(y0, y1, x);
            var expectedSlope = y1 - y0;
            var actualSlope = (y - y0) / x;
            assertEquals(expectedSlope, actualSlope, DELTA);
        }
    }

    @Test
    void clampIntClampsValueBelowMin() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextInt(-100_000, 100_000);
            var max = min + rng.nextInt(100_000);
            var v = min - rng.nextInt(500);
            assertEquals(min, Math2.clamp(min, v, max));
        }
    }

    @Test
    void clampIntClampsValueAboveMax() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextInt(-100_000, 100_000);
            var max = min + rng.nextInt(100_000);
            var v = max + rng.nextInt(500);
            assertEquals(max, Math2.clamp(min, v, max));
        }
    }

    @Test
    void clampIntPreservesValuesBetweenMinAndMax() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextInt(-100_000, 100_000);
            var v = min + rng.nextInt(100_000);
            var max = v + rng.nextInt(100_000);
            assertEquals(v, Math2.clamp(min, v, max));
        }
    }

    @Test
    void clampDoubleClampsValueBelowMin() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextDouble(-100_000, 100_000);
            var max = min + rng.nextDouble(100_000);
            var v = min - rng.nextDouble(500);
            assertEquals(min, Math2.clamp(min, v, max));
        }
    }

    @Test
    void clampDoubleClampsValueAboveMax() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextDouble(-100_000, 100_000);
            var max = min + rng.nextDouble(100_000);
            var v = max + rng.nextDouble(500);
            assertEquals(max, Math2.clamp(min, v, max));
        }
    }

    @Test
    void clampDoublePreservesValuesBetweenMinAndMax() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var min = rng.nextDouble(-100_000, 100_000);
            var v = min + rng.nextDouble(100_000);
            var max = v + rng.nextDouble(100_000);
            assertEquals(v, Math2.clamp(min, v, max));
        }
    }

    @Test
    void asinhWorksOnKnownValues() {
        var delta = 1e-7;

        var actual1 = Math2.asinh(Math.PI);
        var expected1 = 1.8622957433108482;
        assertEquals(expected1, actual1, delta);

        var actual2 = Math2.asinh(Math.E);
        var expected2 = 1.7253825588523148;
        assertEquals(expected2, actual2, delta);

        var actual3 = Math2.asinh(2022);
        var expected3 = 8.304989641287715;
        assertEquals(expected3, actual3, delta);

        var actual4 = Math2.asinh(-2022);
        var expected4 = -8.304989641057409;
        assertEquals(expected4, actual4, delta);

        var actual5 = Math2.asinh(-1.23456);
        var expected5 = -1.0379112743027366;
        assertEquals(expected5, actual5, delta);
    }

    @Test
    void dotProductOfAVectorWithItselfIsSquaredNorm() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(-100, 100);
            var y = rng.nextDouble(-100, 100);
            var expected = x * x + y * y;
            assertEquals(expected, Math2.dotProduct(x, y, x, y), 1e-7);
        }
    }

    @Test
    void dotProductOfOrthogonalVectorsIsZero() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var x = rng.nextDouble(-100, 100);
            var y = rng.nextDouble(-100, 100);
            assertEquals(0, Math2.dotProduct(x, y, -y, x), 1e-7);
            assertEquals(0, Math2.dotProduct(x, y, y, -x), 1e-7);
        }
    }

    @Test
    void squaredNormIsEqualToSquaredHypot() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var uX = rng.nextDouble(-1000, 1000);
            var uY = rng.nextDouble(-1000, 1000);
            var norm = Math.hypot(uX, uY);
            assertEquals(norm * norm, Math2.squaredNorm(uX, uY), 1e-7);
        }
    }

    @Test
    void normIsEqualToHypot() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var uX = rng.nextDouble(-1000, 1000);
            var uY = rng.nextDouble(-1000, 1000);
            var norm = Math.hypot(uX, uY);
            assertEquals(norm, Math2.norm(uX, uY), 1e-7);
        }
    }

    @Test
    void projectionLengthWorksOnKnownValues() {
        var delta = 1e-7;

        var actual1 = Math2.projectionLength(1, 2, 3, 4, 5, 6);
        var expected1 = 5.65685424949238;
        assertEquals(expected1, actual1, delta);

        var actual2 = Math2.projectionLength(1, 1, 2, 3, 5, 8);
        var expected2 = 8.049844718999243;
        assertEquals(expected2, actual2, delta);
    }
}