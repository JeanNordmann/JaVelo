package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BitsTest {

    public static final double DELTA = 1e-7;

    @Test
    void extractSignedWorkOnData() {
        int initialData = 0b11001010111111101011101010111110;
        int expectedData1 = 0b10101011111000000000000000000000;
        int expectedData2 = 0b11111111111111111111111111111010;
        assertEquals(expectedData2,Bits.extractSigned(initialData,8,4));
    }

    @Test
    void extractUnsignedWorkOnData() {
        int initialData = 0b11001010111111101011101010111110;
        int expectedData1 = 0b10101011111000000000000000000000;
        int expectedData2 = 0b00000000000000000000000000001010;
        assertEquals(expectedData2,Bits.extractUnsigned(initialData,8,4));
    }

}
