package ch.epfl.javelo.data;


import org.junit.jupiter.api.Test;

import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {
    public static void main(String[] args) {
        /*System.out.println(Attribute.COUNT);
        long a = 0b0011_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111L;
        long b = 1;
        a++;
        System.out.println(Long.toBinaryString(a) + " " + a);
        a--;
        System.out.println(Long.toBinaryString(a));
        System.out.println(Long.bitCount(a));
        System.out.println(Long.numberOfLeadingZeros(a));*/
        /*System.out.println((bits >> 5 & 1) == 1);*/

        /*AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1,Attribute.HIGHWAY_TRACK);
        String str = "{";
        StringJoiner j = new StringJoiner(",","{","}");
        for(Attribute attribute : Attribute.ALL){
            if (set.contains(attribute)) System.out.println(attribute);
        }
        System.out.println(set.bits() + " = " + Long.toBinaryString(set.bits()));*/

        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE4,Attribute.MOTORROAD_YES,Attribute.HIGHWAY_FOOTWAY);
        System.out.println(set);
    }

    @Test
    public void ofTest(){

        assertThrows(IllegalArgumentException.class, () -> {
            new AttributeSet(0b1011_1111_1111_0000_1111_1111_1010_1101_1010_1010_0101_1111_0000_1111_0110_0000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet set = new AttributeSet(0b0111_1111_1111_1100_1111_1001_1010_1101_1010_1111_0101_1111_0000_1111_0000_0110L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet set = new AttributeSet(0b1111_1111_1111_0011_1001_1111_1010_1101_1010_1010_0000_1111_0000_1111_1100_0000L);
        });
    }

    @Test
    void constructorThrowsWhenInvalidBits(){
        assertThrows(IllegalArgumentException.class, () ->  {
            new AttributeSet(0b0110_0100_1110_0000_0000_0000_0000_1110_1111_0011_0000_0000_0000_0000_0000_0000L);
        });
    }

    @Test
    public void containsTest(){
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        AttributeSet set2 = AttributeSet.of(Attribute.TRACKTYPE_GRADE4,Attribute.MOTORROAD_YES,Attribute.HIGHWAY_FOOTWAY);

        assertTrue(set.contains(Attribute.HIGHWAY_TRACK));
        assertTrue(set.contains(Attribute.TRACKTYPE_GRADE1));
        assertFalse(set.contains(Attribute.TRACKTYPE_GRADE4));
        assertFalse(set.contains(Attribute.SURFACE_GRAVEL));
        assertTrue(set2.contains(Attribute.TRACKTYPE_GRADE4));
        assertTrue(set2.contains(Attribute.MOTORROAD_YES));
    }

    @Test
    public void toStringTest(){
        AttributeSet set =
                AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}",
                set.toString());
    }



    @Test
    void ofCreatesGoodAttributeSetOnNonTrivialAttributes(){
        AttributeSet attr1 = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.HIGHWAY_TRACK, Attribute.HIGHWAY_RESIDENTIAL);
        assertEquals(new AttributeSet(0b0111L), attr1);
        AttributeSet attr2 = AttributeSet.of(Attribute.HIGHWAY_FOOTWAY, Attribute.HIGHWAY_PATH, Attribute.HIGHWAY_UNCLASSIFIED);
        assertEquals(new AttributeSet(0b0011_1000L), attr2);
    }

    @Test
    void containsWorksOnNonTrivialValue() {
        AttributeSet testBits = new AttributeSet(0b0110_1100_0100L);
        assertEquals(true, testBits.contains(Attribute.HIGHWAY_RESIDENTIAL));
        assertEquals(true, testBits.contains(Attribute.HIGHWAY_TERTIARY));
        assertEquals(false, testBits.contains(Attribute.SURFACE_WOOD));
    }

    @Test
    void intersectsWorksOnNonTrivialValues(){
        AttributeSet set1 = new AttributeSet(0b1001) ;
        AttributeSet set2 = new AttributeSet(0b1010) ;
        AttributeSet set3 = new AttributeSet(0b0101) ;
        AttributeSet set4 = new AttributeSet(0b0100_1000_0101_0001);
        assertEquals(true, set1.intersects(set2));
        assertEquals(false, set2.intersects(set3));
        assertEquals(true, set4.intersects(set1));
        assertEquals(false, set4.intersects(set2));
    }

    @Test
    void toStringWorksProperly() {
        AttributeSet set = AttributeSet.of(Attribute.TRACKTYPE_GRADE1, Attribute.HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }
}
