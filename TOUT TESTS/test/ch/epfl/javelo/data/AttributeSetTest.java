package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {

    @Test
    void toStringWorksWithGivenTest(){
        AttributeSet set =
                AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}",
                set.toString());
    }

    static String expectedStringFromOrderedValues(Attribute... args) {
        String end = "{";
        for (Attribute att : args) {
            end = end.concat(att.keyValue() + ",");
        }
        end = end.substring(0, end.length() - 1);
        end = end.concat("}");
        return end;
    }

    record AttSetTester(Attribute[] ol, Attribute[] ul) {
        @Override
        public String toString() {
            return expectedStringFromOrderedValues(ol);
        }
    }

    @Test
    void ofWorksWithArbitraryValues() {
        AttSetTester[] list = {new AttSetTester(new Attribute[]{ACCESS_YES, ACCESS_PRIVATE, ICN_YES}, new Attribute[]{ICN_YES, ACCESS_PRIVATE, ACCESS_YES}),
                new AttSetTester(new Attribute[]{HIGHWAY_FOOTWAY, ONEWAY_M1, VEHICLE_NO, ACCESS_PRIVATE, CYCLEWAY_OPPOSITE },
                        new Attribute[]{CYCLEWAY_OPPOSITE, ACCESS_PRIVATE, VEHICLE_NO, ONEWAY_M1, HIGHWAY_FOOTWAY}
                )
        };

        for (AttSetTester a : list) {
            assertEquals(a.toString(), AttributeSet.of(a.ul).toString());
        }
    }

    @Test
    void trySomeGivenValueForOf() {
        String expected = "{access=yes,cycleway=opposite,icn=yes}";
        String actual = AttributeSet.of(ICN_YES, CYCLEWAY_OPPOSITE, ACCESS_YES).toString();

        assertEquals(expected, actual);
    }

    @Test
    void containsWorksWithGivenTest() {
        AttributeSet set =
                AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK, BICYCLE_YES);
        assertTrue(set.contains(BICYCLE_YES));
    }

    @Test
    void intersectReturnsTrueWhenTrue() {
        AttributeSet setA =
                AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        AttributeSet setB =
                AttributeSet.of(TRACKTYPE_GRADE1, ACCESS_PRIVATE, NCN_YES);
        assertTrue(setA.intersects(setB));
    }

    @Test
    void intersectReturnsFalseWhenFalse() {
        AttributeSet setA =
                AttributeSet.of(RCN_YES, HIGHWAY_TRACK);
        AttributeSet setB =
                AttributeSet.of(TRACKTYPE_GRADE1, ACCESS_PRIVATE, NCN_YES);
        assertFalse(setA.intersects(setB));
    }

    @Test
    void constructorDoesThrowErrorOnInvalidArg() {
        Long n = 1L << 62L;
        System.out.println(Long.toBinaryString(n));
        assertThrows(IllegalArgumentException.class, () -> {AttributeSet a = new AttributeSet(n);});
    }

    @Test
    void constructorDoesNotThrowErrorOnValidArg() {
        assertDoesNotThrow(() -> {AttributeSet a = new AttributeSet(0x1);});
    }

    @Test
    void attributeSetConstructorThrowsWithInvalidBitsSet() {
        for (int i = 62; i < Long.SIZE; i += 1) {
            var invalidBits = 1L << i;
            assertThrows(IllegalArgumentException.class, () -> {
                new AttributeSet(invalidBits);
            });
        }
    }
}