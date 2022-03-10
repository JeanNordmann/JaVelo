package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {
    private static final int ATTRIBUTES_COUNT = 62;

    @Test
    void attributeSetConstructorWorksWithAllBitsSet() {
        assertDoesNotThrow(() -> {
            var allValidBits = (1L << ATTRIBUTES_COUNT) - 1;
            new AttributeSet(allValidBits);
        });
    }

    @Test
    void attributeSetConstructorThrowsWithInvalidBitsSet() {
        for (int i = ATTRIBUTES_COUNT; i < Long.SIZE; i += 1) {
            var invalidBits = 1L << i;
            assertThrows(IllegalArgumentException.class, () -> {
                new AttributeSet(invalidBits);
            });
        }
    }

    @Test
    void attributeSetOfWorksForEmptySet() {
        assertEquals(0L, AttributeSet.of().bits());
    }

    @Test
    void attributeSetOfWorksForFullSet() {
        var allAttributes = AttributeSet.of(Attribute.values());
        assertEquals((1L << ATTRIBUTES_COUNT) - 1, allAttributes.bits());
        assertEquals(ATTRIBUTES_COUNT, Long.bitCount(allAttributes.bits()));
    }

    @Test
    void attributeSetContainsWorksOnRandomSets() {
        var allAttributes = Attribute.values();
        assert allAttributes.length == ATTRIBUTES_COUNT;
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            Collections.shuffle(Arrays.asList(allAttributes), new Random(rng.nextLong()));
            var count = rng.nextInt(ATTRIBUTES_COUNT + 1);
            var attributes = Arrays.copyOf(allAttributes, count);
            var attributeSet = AttributeSet.of(attributes);
            assertEquals(count, Long.bitCount(attributeSet.bits()));
            for (int j = 0; j < count; j += 1)
                assertTrue(attributeSet.contains(allAttributes[j]));
            for (int j = count; j < ATTRIBUTES_COUNT; j += 1)
                assertFalse(attributeSet.contains(allAttributes[j]));
        }
    }

    @Test
    void attributeSetIntersectsWorksOnRandomSets() {
        var allAttributes = Attribute.values();
        assert allAttributes.length == ATTRIBUTES_COUNT;
        var rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            Collections.shuffle(Arrays.asList(allAttributes), new Random(rng.nextLong()));
            var count = rng.nextInt(1, ATTRIBUTES_COUNT + 1);
            var attributes = Arrays.copyOf(allAttributes, count);
            var attributeSet = AttributeSet.of(attributes);
            var attributeSet1 = AttributeSet.of(attributes[0]);
            assertTrue(attributeSet.intersects(attributeSet1));
            assertTrue(attributeSet1.intersects(attributeSet));
        }
    }

    @Test
    void attributeSetIntersectsWorksOnComplementarySets() {
        var rng = newRandom();
        var validBitsMask = (1L << ATTRIBUTES_COUNT) - 1;
        for (int i = 0; i < RANDOM_ITERATIONS; i += 1) {
            var bits = rng.nextLong();
            var set = new AttributeSet(bits & validBitsMask);
            var setComplement = new AttributeSet(~bits & validBitsMask);
            assertFalse(set.intersects(setComplement));
            assertFalse(setComplement.intersects(set));
            assertTrue(set.intersects(set));
            assertTrue(setComplement.intersects(setComplement));
        }
    }

    @Test
    void attributeSetToStringWorksOnKnownValues() {
        assertEquals("{}", new AttributeSet(0).toString());

        for (var attribute : Attribute.values()) {
            var expected = "{" + attribute + "}";
            assertEquals(expected, AttributeSet.of(attribute).toString());
        }

        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}