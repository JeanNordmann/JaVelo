package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BitsTest {
    @Test
    void bitsExtractThrowsWithInvalidStart() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, -1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, Integer.SIZE, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, -1, 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, Integer.SIZE, 1);
        });
    }

    @Test
    void bitsExtractThrowsWithInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractUnsigned(0, 0, Integer.SIZE);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 10, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Bits.extractSigned(0, 0, Integer.SIZE + 1);
        });
    }

    @Test
    void bitsExtractWorksOnFullLength() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = rng.nextInt();
            assertEquals(v, Bits.extractSigned(v, 0, Integer.SIZE));
        }
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var v = 1 + rng.nextInt(-1, Integer.MAX_VALUE);
            assertEquals(v, Bits.extractUnsigned(v, 0, Integer.SIZE - 1));
        }
    }

    @Test
    void bitsExtractWorksOnRandomValues() {
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var value = rng.nextInt();
            var start = rng.nextInt(0, Integer.SIZE - 1);
            var length = rng.nextInt(1, Integer.SIZE - start);

            var expectedU = (value >> start) & (1 << length) - 1;
            var mask = 1 << (length - 1);
            var expectedS = (expectedU ^ mask) - mask;
            assertEquals(expectedU, Bits.extractUnsigned(value, start, length));
            assertEquals(expectedS, Bits.extractSigned(value, start, length));
        }
    }
}