package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class FunctionsTest {
    @Test
    void functionsConstantIsConstant() {
        var rng = newRandom();
        for (var y : new double[]{Double.NEGATIVE_INFINITY, -20.22, 0, 20.22}) {
            var f = Functions.constant(y);
            for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
                var x = rng.nextDouble(-100_000, 100_000);
                assertEquals(y, f.applyAsDouble(x));
            }
        }
    }

    @Test
    void functionsSampledThrowsWithLessThanTwoSamples() {
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float[]{}, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Functions.sampled(new float[]{0}, 1);
        });
    }

    @Test
    void functionsSampledWorksWhenEvaluatedCloseToXMax() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int l = 2; l < 40; l += 1) {
            var samples = new float[l];
            for (int i = 0; i < samples.length; i += 1)
                samples[i] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(l, 4 * l);
            var f = Functions.sampled(samples, xMax);

            assertDoesNotThrow(() -> {
                var xL = xMax;
                var xH = xMax;
                for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
                    var yL = f.applyAsDouble(xL);
                    var yH = f.applyAsDouble(xH);
                    xL = Math.nextDown(xL);
                    xH = Math.nextUp(xH);
                }
            });
        }
    }

    @Test
    void functionsSampledIsConstantLeftAndRightOfSamples() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 20);
            var samples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1)
                samples[j] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(Math.nextUp(0), 100);
            var f = Functions.sampled(samples, xMax);
            assertEquals(samples[0], f.applyAsDouble(Math.nextDown(0)));
            assertEquals(samples[0], f.applyAsDouble(-1000));
            assertEquals(samples[sampleCount - 1], f.applyAsDouble(Math.nextUp(xMax)));
            assertEquals(samples[sampleCount - 1], f.applyAsDouble(xMax + 1000));
        }
    }

    @Test
    void functionsSampledInterpolatesBetweenSamples() {
        var rng = newRandom();
        var halfWidth = 5000;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 20);
            var samples = new float[sampleCount];
            for (int j = 0; j < sampleCount; j += 1)
                samples[j] = rng.nextFloat(-halfWidth, halfWidth);
            var xMax = rng.nextDouble(50, 100);
            var f = Functions.sampled(samples, xMax);
            var interSampleDistance = xMax / (sampleCount - 1);
            var minDeltaX = interSampleDistance / 4;
            for (int j = 1; j < sampleCount; j += 1) {
                var xL = (j - 1) * interSampleDistance;
                var yL = samples[j - 1];
                var xR = j * interSampleDistance;
                var yR = samples[j];
                var x = rng.nextDouble(xL + minDeltaX, xR - minDeltaX);
                var y = f.applyAsDouble(x);
                var expectedSlope = (yR - yL) / interSampleDistance;
                var actualSlope = (y - yL) / (x - xL);
                assertEquals(expectedSlope, actualSlope, 1e-3);
            }
        }
    }
}
