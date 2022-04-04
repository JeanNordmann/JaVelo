package ch.epfl.javelo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BitsTest {
    private static int getBitLength(int bit) {
        return Integer.numberOfTrailingZeros(Integer.highestOneBit(bit)) + 1;
    }

    @Test
    void getBitLengthDoesWork() {
        assertEquals(4,getBitLength(0b1111));
        assertEquals(10, getBitLength(0b1101110000));
    }

    @Test
    void extractUnsignedWorksOnKnownValues(){
        int value = 0b11001010111111101011101010111110;
        int expected = 0b1010;
        int actual = Bits.extractUnsigned(value,8,4 );
        assertEquals(expected , actual ) ;
    }

    @Test
    void extractSignedWorksOnKnownValues(){
        int value = 0b11001010111111101011101010111110;
        int expected = 0b11111111111111111111111111111010;
        int actual = Bits.extractSigned(value,8,4 );
        assertEquals(expected , actual);
    }



    @Test
    void extractUnsignedDoesThrowIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> {
            // Bits.extractUnsigned(0b11100111, 33, 1);
            // Bits.extractUnsigned(0b110011, 0, 32);
            Bits.extractUnsigned(0b111000000111, 3, -3);
        });
    }

    @Test
    void extractUnsignedWorksOnArbitraryValues() {
        int value = 0b100011001011110110111; //length 21, index 20 19 18 17 … 3 2 1 0
        int[][] expectedTuples = {{0b10001, 16, 5}, {0b11001011, 9, getBitLength(0b11001011)}, {0b110111, 0, getBitLength(0b110111)}}; // format - 0:expected, 1:start, 2:length

        for (int[] list: expectedTuples) {
            int actualVal = Bits.extractUnsigned(value, list[1], list[2]);
            System.out.println("Actual - " + Integer.toBinaryString(actualVal) + " ; Expected - " + Integer.toBinaryString(list[0]));
            assertEquals(list[0], actualVal);
        }
    }

    record ET(int expectedValue, int start) {
        public int length() {
            return getBitLength(expectedValue);
        }

        public String toString() {
            return Integer.toBinaryString(expectedValue);
        }
    }

//    @Test
//    void extractSignedWorksOnArbitraryValues() {
//        ET[] expected = {new ET(0b), new ET()};
//    }
}
