package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;

class Q28_4Test {
    @Test
    void q28_4OfIntWorksWithRandomValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var n = rng.nextInt(1 << 28);
            assertEquals(n, Q28_4.ofInt(n) >>> 4);
        }
    }

    @Test
    void q28_4AsDoubleWorksOnKnownValues() {
        assertEquals(1.0, Q28_4.asDouble(0b1_0000));
        assertEquals(1.5, Q28_4.asDouble(0b1_1000));
        assertEquals(1.25, Q28_4.asDouble(0b1_0100));
        assertEquals(1.125, Q28_4.asDouble(0b1_0010));
        assertEquals(1.0625, Q28_4.asDouble(0b1_0001));
        assertEquals(1.9375, Q28_4.asDouble(0b1_1111));
    }

    @Test
    void q28_4AsFloatWorksOnKnownValues() {
        assertEquals(1.0f, Q28_4.asFloat(0b1_0000));
        assertEquals(1.5f, Q28_4.asFloat(0b1_1000));
        assertEquals(1.25f, Q28_4.asFloat(0b1_0100));
        assertEquals(1.125f, Q28_4.asFloat(0b1_0010));
        assertEquals(1.0625f, Q28_4.asFloat(0b1_0001));
        assertEquals(1.9375f, Q28_4.asFloat(0b1_1111));
    }

    @Test
    void q28_4ofIntAndAsFloatDoubleAreInverse() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var n = rng.nextInt(1 << 24);
            assertEquals(n, Q28_4.asFloat(Q28_4.ofInt(n)));
            assertEquals(n, Q28_4.asDouble(Q28_4.ofInt(n)));
        }
    }
}