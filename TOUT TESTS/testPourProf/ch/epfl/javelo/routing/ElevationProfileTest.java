package ch.epfl.javelo.routing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.DoubleSummaryStatistics;
import java.util.random.RandomGenerator;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElevationProfileTest {

    @Test
    void elevationProfileConstructorThrowsWithNotEnoughSamples() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(1, new float[0]);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(1, new float[]{3.14f});
        });
    }

    @Test
    void elevationProfileConstructorThrowsWithZeroLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ElevationProfile(0, new float[]{1, 2, 3});
        });
    }

    @Test
    void elevationProfileLengthReturnsLength() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var length = Math.nextUp(rng.nextDouble(1000));
            var profile = new ElevationProfile(length, new float[]{1, 2, 3});
            assertEquals(length, profile.length());
        }
    }

    private static float[] randomSamples(RandomGenerator rng, int count) {
        var samples = new float[count];
        for (int i = 0; i < count; i += 1)
            samples[i] = rng.nextFloat(4096);
        return samples;
    }

    @Test
    void elevationProfileMinElevationReturnsMinElevation() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var elevationStatistics = new DoubleSummaryStatistics();
            for (var s : elevationSamples) elevationStatistics.accept(s);
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(elevationStatistics.getMin(), profile.minElevation());
        }
    }

    @Test
    void elevationProfileMaxElevationReturnsMaxElevation() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var elevationStatistics = new DoubleSummaryStatistics();
            for (var s : elevationSamples) elevationStatistics.accept(s);
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(elevationStatistics.getMax(), profile.maxElevation());
        }
    }

    @Test
    void elevationProfileTotalAscentReturnsTotalAscent() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var totalAscent = 0d;
            for (int j = 1; j < sampleCount; j += 1) {
                var d = elevationSamples[j] - elevationSamples[j - 1];
                if (d > 0) totalAscent += d;
            }
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(totalAscent, profile.totalAscent());
        }
    }

    @Test
    void elevationProfileTotalDescentReturnsTotalDescent() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var sampleCount = rng.nextInt(2, 1000);
            var elevationSamples = randomSamples(rng, sampleCount);
            var totalDescent = 0d;
            for (int j = 1; j < sampleCount; j += 1) {
                var d = elevationSamples[j] - elevationSamples[j - 1];
                if (d < 0) totalDescent -= d;
            }
            var profile = new ElevationProfile(1000, elevationSamples);
            assertEquals(totalDescent, profile.totalDescent());
        }
    }

    @Test
    void elevationProfileElevationAtWorksOnKnownValues() {
        var samples = new float[]{
                100.00f, 123.25f, 375.50f, 212.75f, 220.00f, 210.25f
        };
        var profile = new ElevationProfile(1000, samples);

        var actual1 = profile.elevationAt(0);
        var expected1 = 100.0;
        assertEquals(expected1, actual1);

        var actual2 = profile.elevationAt(200);
        var expected2 = 123.25;
        assertEquals(expected2, actual2);

        var actual3 = profile.elevationAt(400);
        var expected3 = 375.5;
        assertEquals(expected3, actual3);

        var actual4 = profile.elevationAt(600);
        var expected4 = 212.75;
        assertEquals(expected4, actual4);

        var actual5 = profile.elevationAt(800);
        var expected5 = 220.0;
        assertEquals(expected5, actual5);

        var actual6 = profile.elevationAt(1000);
        var expected6 = 210.25;
        assertEquals(expected6, actual6);

        var actual7 = profile.elevationAt(500);
        var expected7 = 294.125;
        assertEquals(expected7, actual7);
    }
}